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
public class RouteUpdateForm {

    /**
     * 路由id
     */
    @NotBlank(message = "路由id不能为空")
    private String routeId;


    private String path;


    private String uri;


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
     * 路由断言信息
     */
    private String predicates;

    /**
     * 路由过滤器信息
     */
    private String filters;

}
