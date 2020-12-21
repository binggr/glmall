package com.binggr.glmall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: bing
 * @date: 2020/11/18 15:29
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catelog2Vo {
    private String catalog1Id;//一级父分类id
    private List<Catalog3Vo> catalog3List;//三级子分类
    private String id;
    private String name;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Catalog3Vo{
        private String catalog2Id;//父分类，2级分类id
        private String id;
        private String name;
    }
}
