package com.binggr.glmall.product.vo;

import lombok.Data;

/**
 * @author: bing
 * @date: 2020/10/12 9:46
 */
@Data
public class AttrRespVo extends AttrVo {
    //所属分类名字
    private String catelogName;
    //所属分组名字
    private String groupName;

    //分组路径
    private Long[] catelogPath;
}
