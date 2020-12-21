package com.binggr.glmall.product.vo;

import com.binggr.glmall.product.entity.SkuImagesEntity;
import com.binggr.glmall.product.entity.SkuInfoEntity;
import com.binggr.glmall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;
import org.bouncycastle.asn1.esf.SPuri;

import java.util.List;

/**
 * @author: bing
 * @date: 2020/12/3 13:42
 */
@Data
public class SkuItemVo {
    //1、sku 基本信息 pms_sku_info
    SkuInfoEntity info;

    //TODO 查询
    boolean hasStock = true;

    //2、sku图片信息 pms_sku_images
    List<SkuImagesEntity> images;

    //3、获取spu的销售属性组合
    List<SkuItemSaleAttrVo> saleAttr;

    //4、获取spu的介绍
    SpuInfoDescEntity desc;

    //5、获取spu的规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;

    //当前商品秒杀信息
    SeckillInfoVo seckillInfoVo;

}
