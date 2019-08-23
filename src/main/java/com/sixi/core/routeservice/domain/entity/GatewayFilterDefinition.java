package com.sixi.core.routeservice.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: ZY
 * @Date: 2019/8/12 18:18
 * @Version 1.0
 * @Description: 过滤器定义模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayFilterDefinition {

    /**
     * Filter Name
     */
    private String name;

    /**
     * 对应的路由规则
     */
    private Map<String, String> args = new LinkedHashMap<>();

}