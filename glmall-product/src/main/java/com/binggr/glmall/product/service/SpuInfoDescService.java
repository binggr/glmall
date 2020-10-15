package com.binggr.glmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.binggr.common.utils.PageUtils;
import com.binggr.glmall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-04 23:29:40
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void savaSpuInfoDesc(SpuInfoDescEntity descEntity);
}

