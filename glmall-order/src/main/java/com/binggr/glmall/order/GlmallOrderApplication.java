package com.binggr.glmall.order;

import io.seata.spring.annotation.GlobalTransactional;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 使用RabbitMQ
 * 1、引入amqp场景:RabbitAutoConfiguration就会生效
 * 2、给容器中自动配置
 *         RabbitTemplate AmqpAdmin CachingConnectionFactory RabbitConnectionFactoryBean MessagingTemplateConfiguration
 *         所有属性
 *         @ConfigurationProperties(prefix = "spring.rabbitmq")
 *         RabbitProperties
 *         3、给配置容器中加入spring.rabbitmq XXX
 * 4、@EnableRabbit 使用 开启功能
 * 5、监听消息，使用@RabbitListener:必须有@EnableRabbit
 *    @RabbitListener: 类+方法上
 *     @RabbitListener+@RabbitHandler: 方法上 (重载监听不同方法)
 *
 * 本地事务配置失效问题
 * 同一个对象内事务方法互调默认失效，原因 绕过了代理对象 事务使用代理对象来控制的
 * 解决方案： 使用代理对象来调用事务方法
 *  1）、引用aop-starter spring-boot-starter-aop:引入aspectj
 *  2）、@EnableAspectJAutoProxy:开启 aspectj。以后所有动态代理都是 动态代理 以后的动态代理都是aspectj(即使没有接口也可以创建动态代理). 而不是使用默认jdk动态代理
 *      exposeProxy = true 对外暴露代理对象
 *  3）、用代理对象本类互调 OrderService orderService = (OrderService) AopContext.currentProxy(); 解决本地事务配置失效问题
 *
 * Seata控制分布式事务逻辑
 * 1）、每一个微服务数据库先创建 undo_log 表
 * 2）、安装Seata服务器（事务协调） https://github.com/seata/seata/releases
 * 3）、整合
 *      1、导入依赖 spring-boot-starter-alibaba-seata  seata-all(核心)
 *      2、解压启动seata-server
 *      3、所有想要用到分布式事务的微服务都应该使用seata的DataSourceProxy 代理
 *      4、每个微服务，都必须导入 file.conf registry.conf
 *      spring.cloud.alibaba.seata.tx-service-group=glmall-ware-fescar-service-group
 *      5、启动测试分布式的事务
 *      6、给分布式事务的入口标注@GlobalTransactional
 *      7、每一个远程的小事务 @Transactional
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableFeignClients
@EnableRedisHttpSession
@EnableRabbit
@EnableDiscoveryClient
@MapperScan("com.binggr.glmall.order.dao")
@SpringBootApplication()
public class GlmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlmallOrderApplication.class, args);
    }

}
