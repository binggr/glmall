package com.binggr.glmall.product.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author: bing
 * @date: 2020/11/24 16:58
 */
@Configuration
public class MyRedissonConfig {

    /**
     * 所有对Redisson的操作都是对RedissonClient操作
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() throws IOException{
        //1、创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.51.10:6379");
        //2、根据Config创建出实例
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
