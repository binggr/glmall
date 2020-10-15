package com.binggr.glmall.ware.vo;

import lombok.Data;

/**
 * @author: bing
 * @date: 2020/10/14 11:07
 */
@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
