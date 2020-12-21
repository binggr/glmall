package com.binggr.glmall.ware.vo;

import lombok.Data;

/**
 * @author: bing
 * @date: 2020/12/9 13:29
 */
@Data
public class LockStockResult {
    private Long skuId;
    private Integer num;
    private Boolean locked;
}
