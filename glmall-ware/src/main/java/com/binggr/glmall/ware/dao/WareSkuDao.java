package com.binggr.glmall.ware.dao;

import com.binggr.glmall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-05 11:47:57
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId,@Param("wareId") Long wareId,@Param("skuNum") Integer skuNum);

    Long getSkuStock(Long skuId);

    List<Long> ListWareHasSkuStock(@Param("skuId") Long skuId);

    Long lockSkuStock(@Param("skuId")Long skuId, @Param("wareId")Long wareId, @Param("num")Integer num);

    void unlockStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);
}
