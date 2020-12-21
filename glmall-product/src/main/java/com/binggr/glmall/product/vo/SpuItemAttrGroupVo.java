package com.binggr.glmall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author: bing
 * @date: 2020/12/3 14:58
 */
@Data
@ToString
public class SpuItemAttrGroupVo {
    private String groupName;
    private List<Attr> attrs;
}
