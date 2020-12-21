package com.binggr.glmall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: bing
 * @date: 2020/12/8 17:06
 */
@Data
public class FareResponseVo {
    private MemberAddressVo addressVo;
    private BigDecimal fare;
}
