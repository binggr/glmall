package com.binggr.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: bing
 * @date: 2020/12/14 14:55
 */
@Data
public class SeckillOrderTo {
    private String orderSn;//订单号
    private Long promotionSessionId;//活动场次Id
    private Long skuId;//商品Id
    private BigDecimal seckillPrice;//商品价格
    private Integer num;//商品数量
    private Long memberId;//会员Id
}
