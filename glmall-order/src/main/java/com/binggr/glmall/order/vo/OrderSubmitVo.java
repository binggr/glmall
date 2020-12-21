package com.binggr.glmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: bing
 * @date: 2020/12/8 18:11
 * 封装订单提交的数据
 */
@Data
public class OrderSubmitVo {
    private Long addrId;//收货地址Id
    private Integer payType;//支付方式
    //无需提交需要购买的商品，去购物车再获取一遍
    //优惠，发票 TODO

    private String orderToken;//防重令牌
    private BigDecimal payPrice;//应付价格 验价

    private String note;//订单备注

    //用户相关信息：直接去session中取
}

