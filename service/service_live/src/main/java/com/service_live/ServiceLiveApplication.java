package com.service_live;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.service_live.mapper")
@EnableFeignClients(basePackages = "com.courseFeginService")
public class ServiceLiveApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceLiveApplication.class, args);
    }
}
