package com.sixi.core.routeservice.domain.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: ZY
 * @Date: 2019/8/14 13:51
 * @Version 1.0
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteForm {

    /**
     * 路由id
     */
    @NotBlank(message = "路由id不能为空")
    private String routeId;

    /**
     * 对外路径
     */
    @NotBlank(message = "path不能为空")
    private String path;

    /**
     * 真实路径
     */
    @NotBlank(message = "映射不能为空")
    private String uri;

    /**
     * 指向路径地址类型,url类型 0:注册中心服务 1:其他服务
     */
    @NotNull(message = "指向路径地址不能为空,0:注册中心服务 1:其他服务")
    private Integer type;

    /**
     * 优先级
     */
    private Integer order;

    /**
     * 是否忽略前缀
     */
    private Integer stripPrefix;

    /**
     * 是否加入验签过滤器,0:不加入 1:加入
     */
    private Integer authFilter;

    /**
     * 是否加入缓存过滤器,0:不加入 1:加入
     */
    private Integer cacheFilter;

    /**
     * 路由断言信息
     */
    private String predicates;

    /**
     * 路由过滤器信息
     */
    private String filters;

}
