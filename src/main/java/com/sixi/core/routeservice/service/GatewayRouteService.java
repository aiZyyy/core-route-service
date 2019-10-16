package com.sixi.core.routeservice.service;

import com.sixi.core.routeservice.domain.entity.GatewayRoute;
import com.sixi.core.routeservice.domain.form.*;

import java.util.List;

/**
 * @Author: ZY
 * @Date: 2019/10/15 17:23
 * @Version 1.0
 * @Description:
 */

public interface GatewayRouteService {

    void skipRouteAdd(RouteSkipAddForm routeSkipForm);

    void skipRouteDel(RouteSkipDelForm routeSkipDelForm);

    /**
     * 查询路由信息
     *
     * @param routeId
     * @return
     */
    GatewayRoute findOneByRouteId(String routeId);

    /**
     * 查询所有路由信息
     *
     * @return
     */
    List<GatewayRoute> findListAll();

    /**
     * 新增路由
     */
    GatewayRoute addRoute(RouteForm routeForm);

    /**
     * 修改路由
     */
    GatewayRoute updateRoute(RouteUpdateForm routeUpdateForm);

    /**
     * 停用路由
     */
    Integer delRoute(RouteDelForm routeForm);
}
