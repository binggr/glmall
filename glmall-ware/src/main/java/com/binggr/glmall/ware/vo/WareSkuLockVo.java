package com.binggr.glmall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author: bing
 * @date: 2020/12/9 13:22
 */
@Data
public class WareSkuLockVo {

    private String orderSn;//订单号

    private List<OrderItemVo> locks;//需要锁住的所有库存信息

}
