package com.sixi.core.routeservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Created with IntelliJ IDEA
 *
 * @author MiaoWoo
 */
@SpringCloudApplication
@MapperScan("com.sixi.**.mapper")
@EnableFeignClients("com.sixi")
public class CoreRouteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoreRouteServiceApplication.class, args);
    }
}
