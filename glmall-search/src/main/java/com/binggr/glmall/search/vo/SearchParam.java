package com.binggr.glmall.search.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: bing
 * @date: 2020/11/26 14:46
 *
 * 封装页面所有可能传递过来的条件
 *
 */
@Data
public class SearchParam implements Serializable{
    private String keyword;//全文检索关键字
    private Long catalog3Id;//三级分类Id

    /**
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
     */
    private String sort;//排序条件

    /**
     * 过滤条件
     * hasStock=0/1、skuPrice、brandId、catalog3Id、attrs
     * skuPrice=1_500/_500/500_
     * brandId=1...
     * attrs=2_5寸:6寸
     *
     */
    private Integer hasStock;//是否只显示有货
    private String price;//价格区间
    private List<Long> brandId;//按照品牌进行筛选
    private List<String> attrs;//按照属性进行筛选
    private Integer pageNum = 1;//页码

    private String _queryString;//原生的所有查询条件
}
