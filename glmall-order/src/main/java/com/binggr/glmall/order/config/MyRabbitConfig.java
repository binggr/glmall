package com.binggr.glmall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author: bing
 * @date: 2020/12/7 14:13
 */
@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制RabbitTemplate
     * 1、服务器收到消息就回调
     *      1、spring.rabbitmq.publisher-returns=true
     *      2、设置确认回调
     * 2、消息正确抵达队列进行回调
     *      1、spring.rabbitmq.publisher-returns=true
     *         spring.rabbitmq.template.mandatory=true
     * 3、消费段确认(保证每个消息被正确消费。此时才可以broker删除消息)
     *      手动ack消息 spring.rabbitmq.listener.simple.acknowledge-mode=manual
     *      1、默认是自动确认的，只要接收到消息。客户端自动确认。服务端就会移除这个消息
     *      问题：
     *          我们收到很多消息，自动回复给服务器ack,只有一个消息处理成功，宕机了，发生消息丢失：
     *          消费者手动确认模式。只要我们没有明确告诉MQ，货物被签收。相当于没有被ack。消息就一直是unacked的。
     *          即使consumer宕机。不会丢失消息。会重新变为ready状态，下一次有新的consumer就发给它
     *      2、如何签收
     *          channel.basicAck(tag,false);
     *          channel.basicNack(tag,false,true);
     *
     */
    @PostConstruct //对象创建完成以后执行这个方法
    public void initRabbitTemplate(){
        //设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 1、只要消息抵达服务器Broker ack=true
             * @param correlationData 当前消息的唯一关联数据（这个是消息的唯一id）
             * @param b 消息是否成功收到ack
             * @param s 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                //服务器收到了
                System.out.println("confirm...correlationData="+correlationData+",ack="+b+",cause="+s);
            }
        });
        //设置消息抵达队列的确认回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 只要消息没有投递给指定的队列，就触发这个失败回调
             * @param message 投递失败的消息详细信息
             * @param i       回复的状态码
             * @param s       回复的文本内容
             * @param s1      当时这个消息发给哪个交换机
             * @param s2      当时这个消息用哪个路由键
             */
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                /**
                 * 可靠性
                 * 消息丢失
                 * 1、做好消息确认。(publish,consumer[手动ack])
                 * 2、每一个发送的消息都在数据库做好记录。定期将失败的消息再发送一遍。
                 * 消息重复
                 * 将业务消费接口设计为幂等的或者使用防重表
                 * 消息积压
                 * 消费端能力不足积压、发送端发送流量太大
                 * 上线更多的消费者 上线专门的队列消费服务
                 */
                //报错了，修改数据库当前消息的错误状态
                System.out.println("Fail Message["+message+"],replyCode="+i+",replyText="+s+",exchange="+s1+",rountingKey="+s2);
            }
        });
    }

}
