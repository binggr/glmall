package com.binggr.glmall.order.listener;

import com.binggr.glmall.order.entity.OrderEntity;
import com.binggr.glmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * @author: bing
 * @date: 2020/12/10 19:35
 */
@RabbitListener(queues = "order.release.order.queue")
@Service
public class OrderCloseListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void listener(OrderEntity entity, Message message, Channel channel) throws IOException {
        System.out.println("收到过期的订单信息,准备关闭订单"+entity.getOrderSn());
        try{
            orderService.closeOrder(entity);
            //TODO 手动调用支付宝收单，保证一致性
            //TODO 对账 排除账单纠纷
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }
}
