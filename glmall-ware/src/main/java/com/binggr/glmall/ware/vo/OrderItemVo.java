package com.binggr.glmall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: bing
 * @date: 2020/12/7 20:36
 */
@Data
public class OrderItemVo {
    private Long skuId;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;//商品数量
    private BigDecimal totalPrice;

    //TODO 查询库存状态、重量
    private BigDecimal weight;
}
