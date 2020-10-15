package com.binggr.glmall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class GlmallThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlmallThirdPartyApplication.class, args);
    }
}
