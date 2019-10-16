package com.sixi.core.routeservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sixi.core.routeservice.domain.entity.GatewayRoute;
import com.sixi.core.routeservice.domain.entity.GatewayRouteExample;
import com.sixi.core.routeservice.domain.form.*;
import com.sixi.core.routeservice.kit.DozerBeanKit;
import com.sixi.core.routeservice.mapper.GatewayRouteMapper;
import com.sixi.core.routeservice.service.GatewayRouteService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

/**
 * @Author: ZY
 * @Date: 2019/10/15 17:23
 * @Version 1.0
 * @Description:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GatewayRouteServiceImpl implements GatewayRouteService, ApplicationEventPublisherAware {

    public static final String GATEWAY_ROUTES = "gateway_routes";

    public static final String SKIP_ROUTES = "skip_routes";

    private ApplicationEventPublisher publisher;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    private GatewayRouteMapper gatewayRouteMapper;

    /**
     * 可跳过验签路径添加
     *
     * @param routeSkipForm
     */
    @Override
    public void skipRouteAdd(RouteSkipAddForm routeSkipForm) {
        String skipRoute = routeSkipForm.getSkipRoute();
        redisTemplate.opsForHash().put(SKIP_ROUTES, skipRoute, "0");
    }

    /**
     * 可跳过验签路径删除
     *
     * @param routeSkipDelForm
     */
    @Override
    public void skipRouteDel(RouteSkipDelForm routeSkipDelForm) {
        String skipRoute = routeSkipDelForm.getSkipRoute();
        redisTemplate.opsForHash().delete(SKIP_ROUTES, skipRoute);
    }

    @Override
    public GatewayRoute findOneByRouteId(String routeId) {
        GatewayRouteExample gatewayRouteExample = new GatewayRouteExample();
        gatewayRouteExample.createCriteria().andRouteIdEqualTo(routeId);
        List<GatewayRoute> gatewayRoutes = gatewayRouteMapper.selectByExample(gatewayRouteExample);
        return gatewayRoutes.get(0);
    }

    @Override
    public List<GatewayRoute> findListAll() {
        GatewayRouteExample gatewayRouteExample = new GatewayRouteExample();
        List<GatewayRoute> gatewayRoutes = gatewayRouteMapper.selectByExample(gatewayRouteExample);
        return gatewayRoutes;
    }

    @Override
    public GatewayRoute addRoute(RouteForm routeForm) {
        String routeId = routeForm.getRouteId();
        GatewayRoute oneByRouteId = findOneByRouteId(routeId);
        if (Objects.isNull(oneByRouteId)) {
            GatewayRoute gatewayRoute = DozerBeanKit.map(routeForm, GatewayRoute.class);
            //插入数据库
            gatewayRouteMapper.insertSelective(gatewayRoute);
            //存入redis
            RouteDefinition routeDefinition = assembleRouteDefinition(gatewayRoute);
            redisTemplate.opsForHash().put(GATEWAY_ROUTES, routeId, JSON.toJSONString(routeDefinition));
            return gatewayRoute;
        } else {
            return null;
        }
    }

    @Override
    public GatewayRoute updateRoute(RouteUpdateForm routeForm) {
        String routeId = routeForm.getRouteId();
        GatewayRouteExample gatewayRouteExample = new GatewayRouteExample();
        gatewayRouteExample.createCriteria().andRouteIdEqualTo(routeId);
        List<GatewayRoute> gatewayRoutes = gatewayRouteMapper.selectByExample(gatewayRouteExample);
        if (CollectionUtils.isNotEmpty(gatewayRoutes)) {
            GatewayRoute gatewayRoute = DozerBeanKit.map(routeForm, GatewayRoute.class);
            //修改数据库信息
            gatewayRouteMapper.updateByExampleSelective(gatewayRoute, gatewayRouteExample);
            GatewayRoute oneByRouteId = findOneByRouteId(routeId);
            RouteDefinition routeDefinition = assembleRouteDefinition(oneByRouteId);
            //如果为启用状态路由,更新redis信息
            if (oneByRouteId.getEnable() == 0) {
                redisTemplate.opsForHash().put(GATEWAY_ROUTES, routeId, JSON.toJSONString(routeDefinition));
            }
            return gatewayRoute;
        } else {
            return null;
        }
    }

    @Override
    public Integer openRoute(RouteIdForm routeForm) {
        String routeId = routeForm.getRouteId();
        GatewayRouteExample gatewayRouteExample = new GatewayRouteExample();
        gatewayRouteExample.createCriteria().andRouteIdEqualTo(routeId).andEnableEqualTo(1);
        List<GatewayRoute> gatewayRoutes = gatewayRouteMapper.selectByExample(gatewayRouteExample);
        if (CollectionUtils.isNotEmpty(gatewayRoutes)) {
            GatewayRoute gatewayRoute = GatewayRoute.builder().enable(0).build();
            int update = gatewayRouteMapper.updateByExampleSelective(gatewayRoute, gatewayRouteExample);
            RouteDefinition routeDefinition = assembleRouteDefinition(gatewayRoutes.get(0));
            redisTemplate.opsForHash().put(GATEWAY_ROUTES, routeForm.getRouteId(), JSON.toJSONString(routeDefinition));
            return update;
        } else {
            return 0;
        }
    }

    @Override
    public Integer delRoute(RouteIdForm routeForm) {
        String routeId = routeForm.getRouteId();
        GatewayRouteExample gatewayRouteExample = new GatewayRouteExample();
        gatewayRouteExample.createCriteria().andRouteIdEqualTo(routeId).andEnableEqualTo(0);
        List<GatewayRoute> gatewayRoutes = gatewayRouteMapper.selectByExample(gatewayRouteExample);
        if (CollectionUtils.isNotEmpty(gatewayRoutes)) {
            GatewayRoute gatewayRoute = GatewayRoute.builder().enable(1).build();
            int update = gatewayRouteMapper.updateByExampleSelective(gatewayRoute, gatewayRouteExample);
            return update;
        } else {
            return 0;
        }
    }


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }


    /**
     * 转换为RouteDefinition类型
     *
     * @param gatewayRoute
     * @return
     */
    private RouteDefinition assembleRouteDefinition(GatewayRoute gatewayRoute) {
        RouteDefinition definition = new RouteDefinition();
        //获取映射路径
        String path = gatewayRoute.getPath();
        //设定routeId
        definition.setId(gatewayRoute.getRouteId());
        String url;
        //判断uri是否为注册中心服务
        if (0 == gatewayRoute.getType()) {
            //为注册中心服务
            url = "lb://" + gatewayRoute.getUri();
        } else {
            url = gatewayRoute.getUri();
        }
        //设定URI
        URI uri = UriComponentsBuilder.fromUriString(url).build().toUri();
        definition.setUri(uri);

        //设定order
        Integer order = gatewayRoute.getOrder();
        definition.setOrder(order);

        // Filters
        List<FilterDefinition> fdList = new ArrayList<>();
        //获取路径是否去掉前缀
        Integer stripPrefix = gatewayRoute.getStripPrefix();
        //去掉前缀
        if (0 != stripPrefix) {
            //添加去掉前缀过滤器
            FilterDefinition stripFilter = new FilterDefinition();
            Map<String, String> stripParams = new HashMap<>(8);
            stripFilter.setName("StripPrefix");
            stripParams.put("_genkey_0", gatewayRoute.getStripPrefix().toString());
            stripFilter.setArgs(stripParams);
            fdList.add(stripFilter);
        }
        //添加验签过滤器
        FilterDefinition authFilter = new FilterDefinition();
        Map<String, String> authParams = new HashMap<>(8);
        authParams.put("auth-filter", "#{@authorizationFilter}");
        authFilter.setArgs(authParams);
        fdList.add(authFilter);
        //获取额外filter
        String filters = gatewayRoute.getFilters();
        if (null != filters) {
            Object parse = JSON.parseObject(filters);
            JSONArray filtersArray = (JSONArray) parse;
            List<FilterDefinition> list = JSONObject.parseArray(filtersArray.toJSONString(), FilterDefinition.class);
            fdList.addAll(list);
        }
        //设定filters
        definition.setFilters(fdList);

        //Predicates
        List<PredicateDefinition> pdList = new ArrayList<>();
        PredicateDefinition predicate = new PredicateDefinition();
        Map<String, String> predicateParams = new HashMap<>(8);
        predicate.setName("Path");
        predicateParams.put("pattern", gatewayRoute.getPath());
        predicateParams.put("pathPattern", gatewayRoute.getPath());
        predicate.setArgs(predicateParams);
        pdList.add(predicate);
        //获取额外Predicates
        String predicates = gatewayRoute.getPredicates();
        if (null != predicates) {
            Object parse = JSON.parseObject(predicates);
            JSONArray filtersArray = (JSONArray) parse;
            List<PredicateDefinition> list = JSONObject.parseArray(filtersArray.toJSONString(), PredicateDefinition.class);
            pdList.addAll(list);
        }
        //设定Predicates
        definition.setPredicates(pdList);

        System.out.println("definition:" + JSON.toJSONString(definition));
        return definition;
    }
}
