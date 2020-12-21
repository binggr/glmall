package com.binggr.glmall.order;

import com.binggr.glmall.order.entity.OrderEntity;
import com.binggr.glmall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

@Slf4j
@SpringBootTest
class GlmallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void sendMessageTemplate(){
        //如果发送的消息是个对象 会使用序列化机制 将对象写出去
        //如果想要将对象转换为JSON
        //1、发送消息
        String msg = "helloworld";
        for (int i=0;i<10;i++){
            if(i%2==0){
                OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
                reasonEntity.setId(1L);
                reasonEntity.setCreateTime(new Date());
                reasonEntity.setName("哈哈"+i);
                rabbitTemplate.convertAndSend("hello.java.exchange","hello.java",reasonEntity);
            }else {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello.java.exchange","hello.java",orderEntity);
            }

            log.info("消息发送完成{}",msg);
        }

    }

    /**
     * 1、如何创建Exchange【hello.java.exchange】、Queue、Binding
     * 1）、使用AmqpAdmin进行创建
     * 2、如何收发消息
     */
    @Test
    void contextLoads() {
        //Exchange
        DirectExchange directExchange = new DirectExchange("hello.java.exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange创建成功：[{}]", "hello.java.exchange");
    }

    @Test
    void CreateQueue() {
        // public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments) {
        Queue queue = new Queue("hello.java.queue", true, false, false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue创建成功：[{}]", "hello.java.queue");
    }

    @Test
    void CreateBinding() {
        /**
         * public Binding(String destination, 目的地
         * Binding.DestinationType destinationType, 目的类型
         * String exchange, 交换机
         * String routingKey, 路由键
         * @Nullable Map<String, Object> arguments 自定义参数
         * )
         * 将Exchange和destination目的地进行绑定，使用routingKey作为路由键
         */
        Binding binding = new Binding("hello.java.queue",
                Binding.DestinationType.QUEUE,
                "hello.java.exchange",
                "hello.java",
                null);
        amqpAdmin.declareBinding(binding);
        log.info("Binding创建成功：[{}]", "hello.java.queue");
    }

}
