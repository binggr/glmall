package com.binggr.glmall.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.binggr.common.to.mq.OrderTo;
import com.binggr.common.to.mq.StockDetailTo;
import com.binggr.common.to.mq.StockLockedTo;
import com.binggr.common.utils.R;
import com.binggr.glmall.ware.entity.WareOrderTaskDetailEntity;
import com.binggr.glmall.ware.entity.WareOrderTaskEntity;
import com.binggr.glmall.ware.service.WareSkuService;
import com.binggr.glmall.ware.vo.OrderVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author: bing
 * @date: 2020/12/10 18:01
 */
@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {

    @Autowired
    WareSkuService wareSkuService;

    /**
     * 库存自动解锁
     * 1、下订单成功，库存锁定成功，接下来的业务调用失败，导致库存回滚。之前锁定的库存就要自动解锁。
     * 2、下订单失败，锁库存失败
     *
     * 只要解锁库存的消息失败。一定要告诉服务器此次解锁失败。启动手动ack机制
     *
     * @param to
     * @param message
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        System.out.println("收到解锁库存的消息");
        try{
            wareSkuService.unlockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }

    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo orderTo,Message message, Channel channel) throws IOException {
        System.out.println("订单关闭准备解锁库存");
        try{
            //当前消息是否是第二次或以后(重新)派发的
            //Boolean redelivered = message.getMessageProperties().getRedelivered();
            wareSkuService.unlockStock(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }


}
