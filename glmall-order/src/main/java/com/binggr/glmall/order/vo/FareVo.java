package com.binggr.glmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: bing
 * @date: 2020/12/8 17:06
 */
@Data
public class FareVo {
    private MemberAddressVo addressVo;
    private BigDecimal fare;
}
