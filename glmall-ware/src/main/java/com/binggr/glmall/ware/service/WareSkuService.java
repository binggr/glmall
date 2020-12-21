package com.binggr.glmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.binggr.common.to.mq.OrderTo;
import com.binggr.common.to.mq.StockLockedTo;
import com.binggr.common.utils.PageUtils;
import com.binggr.glmall.ware.entity.WareSkuEntity;
import com.binggr.glmall.ware.vo.LockStockResult;
import com.binggr.glmall.ware.vo.SkuHasStockVo;
import com.binggr.glmall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-05 11:47:57
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    Boolean orderLockStock(WareSkuLockVo vo);

    void unlockStock(StockLockedTo to);

    void unlockStock(OrderTo orderTo);
}

