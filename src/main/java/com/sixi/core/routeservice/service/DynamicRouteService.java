package com.sixi.core.routeservice.service;

import com.alibaba.fastjson.JSON;
import com.sixi.core.routeservice.domain.form.RouteForm;
import com.sixi.core.routeservice.domain.form.RouteDelForm;
import com.sixi.core.routeservice.domain.form.RouteSkipAddForm;
import com.sixi.core.routeservice.domain.form.RouteSkipDelForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: ZY
 * @Date: 2019/8/12 17:20
 * @Version 1.0
 * @Description:
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class DynamicRouteService implements ApplicationEventPublisherAware {

    public static final String GATEWAY_ROUTES = "gateway_routes";

    public static final String SKIP_ROUTES = "skip_routes";

    private ApplicationEventPublisher publisher;

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 刷新路由信息
     */
    public void notifyChanged() {
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 可跳过验签路径添加
     *
     * @param routeSkipForm
     */
    public void skipRouteAdd(RouteSkipAddForm routeSkipForm) {
        String skipRoute = routeSkipForm.getSkipRoute();
        redisTemplate.opsForHash().put(SKIP_ROUTES, skipRoute, "0");
    }

    /**
     * 可跳过验签路径删除
     *
     * @param routeSkipDelForm
     */
    public void skipRouteDel(RouteSkipDelForm routeSkipDelForm) {
        String skipRoute = routeSkipDelForm.getSkipRoute();
        redisTemplate.opsForHash().delete(SKIP_ROUTES, skipRoute);
    }

    /**
     * 更新或添加路由
     *
     * @param routeForm
     * @return
     */
    public String upsert(RouteForm routeForm) {
        String routeId = routeForm.getRouteId();
        RouteDefinition definition = assembleRouteDefinition(routeForm);
        try {
            //放入内存
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            redisTemplate.opsForHash().put(GATEWAY_ROUTES, routeId, JSON.toJSONString(definition));
            Query query = new Query(Criteria.where("id").is(routeId));
            mongoTemplate.remove(query, "routePath");
            mongoTemplate.insert(JSON.toJSONString(definition), "routePath");
        } catch (Exception e) {
            e.printStackTrace();
            return "update fail,not find route  routeId: " + routeForm.getRouteId();
        }
        return "update success";
    }

    /**
     * 删除路由
     *
     * @param routeDelForm
     * @return
     */
    public String delete(RouteDelForm routeDelForm) {
        String routeId = routeDelForm.getRouteId();
        try {
            this.routeDefinitionWriter.delete(Mono.just(routeDelForm.getRouteId()));
            //删除redis信息
            redisTemplate.opsForHash().delete(GATEWAY_ROUTES, routeId);
            //删除mongo信息
            Query query = new Query(Criteria.where("id").is(routeId));
            mongoTemplate.remove(query, "routePath");
            notifyChanged();
            return "delete success";
        } catch (Exception e) {
            e.printStackTrace();
            return "delete fail";
        }

    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    /**
     * 转换为RouteDefinition类型
     *
     * @param routeForm
     * @return
     */
    private RouteDefinition assembleRouteDefinition(RouteForm routeForm) {
        String path = routeForm.getPath();

        RouteDefinition definition = new RouteDefinition();

        // ID
        definition.setId(routeForm.getRouteId());

        // Predicates
        List<PredicateDefinition> pdList = new ArrayList<>();
        PredicateDefinition predicate = new PredicateDefinition();
        Map<String, String> pArgs = new LinkedHashMap<>();
        pArgs.put("pattern", "/" + path + "/**");
        predicate.setArgs(pArgs);
        predicate.setName("Path");
        pdList.add(predicate);
        definition.setPredicates(pdList);

        // Filters
        List<FilterDefinition> fdList = new ArrayList<>();
        FilterDefinition filter = new FilterDefinition();
        Map<String, String> fArgs = new LinkedHashMap<>();
        fArgs.put("regexp", "/" + path + "/(?<remaining>.*)");
        fArgs.put("replacement", "/${remaining}");
        filter.setArgs(fArgs);
        filter.setName("RewritePath");
        fdList.add(filter);

        definition.setFilters(fdList);

        String url;
        //判断uri是否为注册中心服务
        if (1 == routeForm.getType()) {
            //为注册中心服务
            url = "lb://" + routeForm.getUri();
        } else {
            url = routeForm.getUri();
        }

        // URI
        URI uri = UriComponentsBuilder.fromUriString(url).build().toUri();
        definition.setUri(uri);

        return definition;
    }

}