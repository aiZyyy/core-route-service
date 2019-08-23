package com.sixi.core.routeservice.controller;


import com.sixi.core.routeservice.api.RouteServiceApi;
import com.sixi.core.routeservice.domain.form.RouteAddForm;
import com.sixi.core.routeservice.domain.form.RouteDelForm;
import com.sixi.core.routeservice.service.DynamicRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Author: ZY
 * @Date: 2019/8/12 18:15
 * @Version 1.0
 * @Description:
 */
@RestController
public class RouteController implements RouteServiceApi {

    @Autowired
    private DynamicRouteService dynamicRouteService;

    @Override
    @PostMapping("/route/notify")
    public String notifyChanged() {
        dynamicRouteService.notifyChanged();
        return "notify_success";
    }

    /**
     * 增加路由
     *
     * @param routeAddForm
     * @return
     */
    @Override
    @PostMapping("/route/add")
    public String add(@Valid @RequestBody RouteAddForm routeAddForm) {
        try {
            return this.dynamicRouteService.add(routeAddForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    @Override
    @PostMapping("/route/update")
    public String update(@RequestBody RouteAddForm routeAddForm) {
        return this.dynamicRouteService.update(routeAddForm);
    }

    @Override
    @PostMapping("/route/delete")
    public String delete(@Valid @RequestBody RouteDelForm routeDelForm) {
        return this.dynamicRouteService.delete(routeDelForm);
    }
}
