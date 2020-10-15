package com.binggr.glmall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author: bing
 * @date: 2020/10/13 22:05
 */
@Data
public class MergeVo {
//    purchaseId: 1, //整单id
//    items:[1,2,3,4] //合并项集合
    private Long purchaseId;
    private List<Long> items;
}
