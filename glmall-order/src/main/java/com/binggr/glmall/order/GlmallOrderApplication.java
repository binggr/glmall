package com.binggr.glmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@MapperScan("com.binggr.glmall.order.dao")
@SpringBootApplication
public class GlmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlmallOrderApplication.class, args);
    }

}
