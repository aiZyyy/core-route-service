package com.sixi.core.routeservice.controller;


import com.sixi.core.routeservice.domain.form.RouteForm;
import com.sixi.core.routeservice.domain.form.RouteDelForm;
import com.sixi.core.routeservice.domain.form.RouteSkipAddForm;
import com.sixi.core.routeservice.domain.form.RouteSkipDelForm;
import com.sixi.core.routeservice.service.DynamicRouteService;
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
public class RouteController {

    @Autowired
    private DynamicRouteService dynamicRouteService;

    /**
     * 路由信息刷新
     * @return
     */
    @PostMapping("/route/notify")
    public String notifyChanged() {
        dynamicRouteService.notifyChanged();
        return "notify_success";
    }

    /**
     * 可跳过验签路径添加
     *
     * @param routeSkipForm
     */
    @PostMapping("/route/skiprouteadd")
    public String skipRouteAdd(@Valid @RequestBody RouteSkipAddForm routeSkipForm){
        dynamicRouteService.skipRouteAdd(routeSkipForm);
        return "success";
    }

    /**
     * 可跳过验签路径删除
     *
     * @param routeSkipDelForm
     */
    @PostMapping("/route/skiproutedel")
    public String skipRouteDel(@Valid @RequestBody RouteSkipDelForm routeSkipDelForm){
        dynamicRouteService.skipRouteDel(routeSkipDelForm);
        return "del success";
    }

    /**
     * 路由信息添加或修改
     * @param routeForm
     * @return
     */
    @PostMapping("/route/upsert")
    public String upsert(@Valid @RequestBody RouteForm routeForm) {
        return this.dynamicRouteService.upsert(routeForm);
    }

    /**
     * 路由信息删除
     * @param routeDelForm
     * @return
     */
    @PostMapping("/route/delete")
    public String delete(@Valid @RequestBody RouteDelForm routeDelForm) {
        return this.dynamicRouteService.delete(routeDelForm);
    }
}
