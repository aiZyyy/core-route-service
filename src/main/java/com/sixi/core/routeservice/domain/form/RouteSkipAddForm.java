package com.sixi.core.routeservice.domain.form;

/**
 * @Author: ZY
 * @Date: 2019/9/23 16:13
 * @Version 1.0
 * @Description:
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteSkipAddForm {

    @NotBlank(message = "跳过路径不能为空")
    private String skipRoute;
}
