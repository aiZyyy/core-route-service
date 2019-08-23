package com.sixi.core.routeservice.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: ZY
 * @Date: 2019/8/12 18:19
 * @Version 1.0
 * @Description: 路由断言定义模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayPredicateDefinition {

    /**
     * 断言对应的Name
     */
    private String name;

    /**
     * 配置的断言规则
     */
    private Map<String, String> args = new LinkedHashMap<>();

}
