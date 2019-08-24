package com.sixi.core.routeservice.service;

import com.alibaba.fastjson.JSON;
import com.sixi.core.routeservice.domain.form.RouteAddForm;
import com.sixi.core.routeservice.domain.form.RouteDelForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
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
public class DynamicRouteService implements ApplicationEventPublisherAware {

    public static final String GATEWAY_ROUTES = "gateway_routes";

    private ApplicationEventPublisher publisher;

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;


    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 刷新路由信息
     */
    public void notifyChanged() {
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 增加路由
     *
     * @param routeAddForm
     * @return
     */
    public String add(RouteAddForm routeAddForm) {
        RouteDefinition definition = assembleRouteDefinition(routeAddForm);
        //放入内存
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        //存入redis
        redisTemplate.opsForHash().put(GATEWAY_ROUTES, definition.getId(), JSON.toJSONString(definition));
        notifyChanged();
        return "success";
    }

    /**
     * 更新路由
     *
     * @param routeAddForm
     * @return
     */
    public String update(RouteAddForm routeAddForm) {
        try {
            this.routeDefinitionWriter.delete(Mono.just(routeAddForm.getRouteId()));
            //删除redis信息
            redisTemplate.opsForHash().delete(GATEWAY_ROUTES, routeAddForm.getRouteId());
        } catch (Exception e) {
            return "update fail,not find route  routeId: " + routeAddForm.getRouteId();
        }
        try {
            add(routeAddForm);
        } catch (Exception e) {
            return "update route fail";
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
        try {
            this.routeDefinitionWriter.delete(Mono.just(routeDelForm.getRouteId()));
            //删除redis信息
            redisTemplate.opsForHash().delete(GATEWAY_ROUTES, routeDelForm.getRouteId());
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
     * @param routeAddForm
     * @return
     */
    private RouteDefinition assembleRouteDefinition(RouteAddForm routeAddForm) {
        String path = routeAddForm.getPath();

        RouteDefinition definition = new RouteDefinition();

        // ID
        definition.setId(routeAddForm.getRouteId());

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
        if (1 == routeAddForm.getType()) {
            //为注册中心服务
            url = "lb://" + routeAddForm.getUri();
        } else {
            url = "https://" + routeAddForm.getUri();
        }

        // URI
        URI uri = UriComponentsBuilder.fromUriString(url).build().toUri();
        definition.setUri(uri);

        return definition;
    }

}