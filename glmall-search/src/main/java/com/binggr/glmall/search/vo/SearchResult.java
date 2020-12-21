package com.binggr.glmall.search.vo;

import com.binggr.common.to.es.SkuEsModel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: bing
 * @date: 2020/11/26 15:10
 *
 * 以下是返回给页面的信息
 */
@Data
public class SearchResult implements Serializable{
    //查询到的所有商品信息
    private List<SkuEsModel> products;

    private Integer pageNum;//当前页码
    private Long total;//总记录数
    private Integer totalPages;//总页码
    private List<Integer> pageNavs;//导航页码
    private List<BrandVo> brands;//当前查询到的结果，所有涉及到的品牌
    private List<CatalogVo> catalogs;//当前查询到的结果，所有涉及到的分类
    private List<AttrVo> attrs;//当前查询到的结果，所涉及到的所有属性

    //面包屑导航
    List<NavVo> navs = new ArrayList<>();
    private List<Long> attrIds = new ArrayList<>();

    @Data
    public static class NavVo{
        private String Name;
        private String navValue;
        private String link;
    }


    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
}
