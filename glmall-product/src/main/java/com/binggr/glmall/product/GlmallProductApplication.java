package com.binggr.glmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients(basePackages = "com.binggr.glmall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.binggr.glmall.product.dao")
@SpringBootApplication
public class GlmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlmallProductApplication.class, args);
    }

}
