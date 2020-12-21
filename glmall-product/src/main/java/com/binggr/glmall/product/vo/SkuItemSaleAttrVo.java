package com.binggr.glmall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author: bing
 * @date: 2020/12/3 15:02
 */
@ToString
@Data
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;
}
