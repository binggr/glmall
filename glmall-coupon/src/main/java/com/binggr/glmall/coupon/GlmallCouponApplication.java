package com.binggr.glmall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@MapperScan("com.binggr.glmall.coupon.dao")
@SpringBootApplication
public class GlmallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlmallCouponApplication.class, args);
    }

}
