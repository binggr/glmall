package com.binggr.glmall.order.to;

import com.binggr.glmall.order.entity.OrderEntity;
import com.binggr.glmall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: bing
 * @date: 2020/12/8 19:49
 */
@Data
public class OrderCreateTo {
    private OrderEntity order;

    private List<OrderItemEntity> orderItems;

    private BigDecimal payPrice;//订单计算的应付价格

    private BigDecimal fare;
}
