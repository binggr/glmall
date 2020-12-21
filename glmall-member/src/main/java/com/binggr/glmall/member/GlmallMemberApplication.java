package com.binggr.glmall.member;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.binggr.glmall.member.feign")
@EnableDiscoveryClient
@MapperScan("com.binggr.glmall.member.dao")
@SpringBootApplication
public class GlmallMemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(GlmallMemberApplication.class, args);
    }

}
