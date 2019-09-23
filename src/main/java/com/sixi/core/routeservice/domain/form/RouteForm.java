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
     * 真实映射路径
     */
    @NotBlank(message = "映射路径不能为空")
    private String uri;

    /**
     * 指向路径地址类型,0为外部地址,1为注册中心地址
     */
    @NotNull(message = "指向路径地址不能为空,0为外部地址,1为注册中心地址")
    private Integer type;
}
