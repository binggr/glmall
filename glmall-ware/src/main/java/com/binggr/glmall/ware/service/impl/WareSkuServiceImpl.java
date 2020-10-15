package com.binggr.glmall.ware.service.impl;

import com.binggr.common.utils.R;
import com.binggr.glmall.ware.feign.ProductFeignService;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.binggr.common.utils.PageUtils;
import com.binggr.common.utils.Query;

import com.binggr.glmall.ware.dao.WareSkuDao;
import com.binggr.glmall.ware.entity.WareSkuEntity;
import com.binggr.glmall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId:
         * wareId:
         */
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skdId = (String) params.get("skdId");
        if(!StringUtils.isEmpty(skdId)){
            queryWrapper.eq("sku_id",skdId);
        }

        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }


        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1、判断如果没有这个库存记录，新增
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id",skuId).eq("ware_id",wareId));
        if(entities == null || entities.size() == 0){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //TODO 远程查询sku名字,如果失败整个事务无需回滚
            //1、自己catch异常
            try{
                R info = productFeignService.info(skuId);
                Map<String,Object> data = (Map<String,Object>) info.get("skuInfo");
                if(info.getCode() == 0){
                    wareSkuEntity.setSkuName((String)data.get("skuName"));
                }
            }catch (Exception e){

            }
            //TODO 让异常出现不回滚

        wareSkuDao.insert(wareSkuEntity);
}else {
        wareSkuDao.addStock(skuId,wareId,skuNum);
        }

        }

}