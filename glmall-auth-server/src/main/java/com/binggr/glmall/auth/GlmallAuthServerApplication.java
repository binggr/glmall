package com.binggr.glmall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 *  核心原理 //TODO
 *  @EnableRedisHttpSession
 *  RedisHttpSessionConfiguration
 *
 *  装饰者模式
 *
 *  自动续期
 *
 */

@EnableRedisHttpSession //整合redis作为session存储
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class GlmallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlmallAuthServerApplication.class, args);
    }

}
