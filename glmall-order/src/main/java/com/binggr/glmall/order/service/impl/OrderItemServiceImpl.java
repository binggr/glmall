package com.binggr.glmall.order.service.impl;

import com.binggr.glmall.order.entity.OrderEntity;
import com.binggr.glmall.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.binggr.common.utils.PageUtils;
import com.binggr.common.utils.Query;

import com.binggr.glmall.order.dao.OrderItemDao;
import com.binggr.glmall.order.entity.OrderItemEntity;
import com.binggr.glmall.order.service.OrderItemService;

@RabbitListener(queues = {"hello.java.queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * queues:声明要监听的所有队列
     * 类型：class org.springframework.amqp.core.Message
     * 参数可以写以下类型
     * 1、Message message 原生消息详细信息
     * 2、T<发送消息的类型> OrderReturnReasonEntity reasonEntity
     * 3、Channel channel 当前传输数据的通道
     *
     * Queue 可以有很多人都来接收 只要收到消息，队列删除消息，而且只有一个人收到消息
     * 场景
     *      1、订单服务启动多个: 同一个消息，只能有一个客户端收到
     *      2、模拟时间很长，能否收到消息。只有一个消息完全处理完，方法结束，才能接收到下一个消息
     */
//    @RabbitListener(queues = {"hello.java.queue"})
    @RabbitHandler
    public void recive(Message message, OrderReturnReasonEntity reasonEntity, Channel channel) throws InterruptedException {
        //Body:'{"id":1,"name":"哈哈","sort":null,"status":null,"createTime":1607321855434}
        byte[] body = message.getBody();//消息体内容
        //headers={__TypeId__=com.binggr.glmall.order.entity.OrderReturnReasonEntity},contentType=application/json, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=true, receivedExchange=hello.java.exchange, receivedRoutingKey=hello.java, deliveryTag=2, consumerTag=amq.ctag-TMzvl_N0eVrTRq6hbLbcxQ, consumerQueue=hello.java.queue])
        MessageProperties messageProperties = message.getMessageProperties();//消息头属性信息
        System.out.println("接收到消息...内容："+message);
//        Thread.sleep(3000);
        System.out.println("消息处理完成!"+reasonEntity.getName());
        //通道按顺序递增
        long tag = message.getMessageProperties().getDeliveryTag();
        //签收货物
        try{
            if(tag%2==0){
                //收货
                channel.basicAck(tag,false);
                System.out.println("签收了货物..."+tag);
            }else {
                //退货
//                channel.basicNack(tag,false,true); 可批量拒绝 服务器重入队列 requeue
//                channel.basicReject(); 拒绝一个
                System.out.println("没签收了货物..."+tag);
            }

        }catch (Exception e){
            //网络中断
        }

    }

    @RabbitHandler
    public void recive1(OrderEntity orderEntity) throws InterruptedException {
        System.out.println("接收到消息...内容2："+orderEntity);
    }


}