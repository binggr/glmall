package com.binggr.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: bing
 * @date: 2020/10/13 16:20
 */
@Data
public class SpuBounsTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;

}
