package com.sixi.core.routeservice.controller;


import com.sixi.core.routeservice.domain.entity.GatewayRoute;
import com.sixi.core.routeservice.domain.form.*;
import com.sixi.core.routeservice.service.GatewayRouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Author: ZY
 * @Date: 2019/8/12 18:15
 * @Version 1.0
 * @Description:
 */
@RestController
@Slf4j
public class RouteController {

    @Autowired
    private GatewayRouteService gatewayRouteService;

    /**
     * 可跳过验签路径添加
     *
     * @param routeSkipForm
     */
    @PostMapping("/route/skip/add")
    public String skipRouteAdd(@Valid @RequestBody RouteSkipAddForm routeSkipForm) {
        gatewayRouteService.skipRouteAdd(routeSkipForm);
        return "success";
    }

    /**
     * 可跳过验签路径删除
     *
     * @param routeSkipDelForm
     */
    @PostMapping("/route/skip/del")
    public String skipRouteDel(@Valid @RequestBody RouteSkipDelForm routeSkipDelForm) {
        gatewayRouteService.skipRouteDel(routeSkipDelForm);
        return "del success";
    }

    /**
     * 路由信息添加
     *
     * @param routeForm
     * @return
     */
    @PostMapping("/route/add")
    public GatewayRoute addRoute(@Valid @RequestBody RouteForm routeForm) {
        return this.gatewayRouteService.addRoute(routeForm);
    }

    /**
     * 路由信息修改
     *
     * @param routeForm
     * @return
     */
    @PostMapping("/route/update")
    public GatewayRoute updateRoute(@Valid @RequestBody RouteUpdateForm routeForm) {
        return this.gatewayRouteService.updateRoute(routeForm);
    }

    /**
     * 路由状态开启
     *
     * @param routeForm
     * @return
     */
    @PostMapping("/route/open")
    public Integer openRoute(@Valid @RequestBody RouteIdForm routeForm) {
        return this.gatewayRouteService.openRoute(routeForm);
    }

    /**
     * 路由状态关闭
     *
     * @param routeDelForm
     * @return
     */
    @PostMapping("/route/close")
    public Integer closeRoute(@Valid @RequestBody RouteIdForm routeDelForm) {
        return this.gatewayRouteService.closeRoute(routeDelForm);
    }
}
