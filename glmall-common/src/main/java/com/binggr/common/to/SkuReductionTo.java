package com.binggr.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: bing
 * @date: 2020/10/13 16:34
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
