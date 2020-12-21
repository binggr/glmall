package com.binggr.glmall.order.vo;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author: bing
 * @date: 2020/12/7 20:27
 */
//订单确认页需要用的数据
public class OrderConfirmVo {
    //收货地址
    @Getter @Setter
    List<MemberAddressVo> address;

    //所有选中的购物项
    @Getter @Setter
    List<OrderItemVo> items;

    //发票记录...

    //优惠劵信息...
    @Getter @Setter
    Integer integration;

    //防重令牌
    @Getter @Setter
    String orderToken;

    @Getter @Setter
    Map<Long, Boolean> stocks;

//    BigDecimal total;//订单总额

    public Integer getCount(){
        Integer i = 0;
        if(items!=null){
            for (OrderItemVo item : items) {
                i+=item.getCount();
            }
        }
        return i;
    }

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if(items!=null){
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }

//    BigDecimal payPrice;//应付价格

    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
