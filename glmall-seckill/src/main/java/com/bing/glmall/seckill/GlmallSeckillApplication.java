package com.bing.glmall.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 1、整合sentinel
 *  1）、导入依赖
 *  2）、下载sentinel的控制台
 *  3）、配置sentinel控制台信息
 *  4）、在控制台调整参数。默认所有的流控设置保存在内存中，重启失效
 *
 * 2、每一个微服务都导入actuator 并配置management.endpoints.web.exposure.include=*
 * 3、自定义sentinel流控返回数据
 *
 * 4、使用Sentinel保护feign远程调用：熔断
 *  1）、调用方法的熔断保护：feign.sentinel.enabled=true
 *  2）、调用方手动指定远程服务的降级策略.远程服务被降级处理，会触发熔断回调方法
 *  3）、超大流量的时候，必须牺牲一些远程服务。在服务的提供方指定一些服务的降级策略。
 *      提供方是运行的，但是不运行自己的业务逻辑。返回是默认的降级数据（限流后的数据）
 *
 * 5、自定义受保护的资源
 *  1)、代码try(Entry entry = SphU.entry("seckillSkus")) }catch (BlockException e){
 *             log.error("资源被限流,{}",e.getMessage());}
 *  2）、基于注解
 *  @SentinelResource(value = "getCurrentSeckillSkusResource",blockHandler = "blockHandler")
 *
 *  无论是1还是2方式 一定要配置被限流以后的默认返回
 *  url可以设置统一返回 BlockExceptionHandler 或者老版本的WebCallbackManager
 *
 *
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GlmallSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlmallSeckillApplication.class, args);
    }

}
