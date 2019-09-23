package com.sixi.core.routeservice.domain.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @Author: ZY
 * @Date: 2019/9/23 16:27
 * @Version 1.0
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteSkipDelForm {
    @NotBlank(message = "跳过路径不能为空")
    private String skipRoute;
}
