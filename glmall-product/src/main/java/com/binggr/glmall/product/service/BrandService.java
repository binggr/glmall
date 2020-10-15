package com.binggr.glmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.binggr.common.utils.PageUtils;
import com.binggr.glmall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-04 23:29:39
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetail(BrandEntity brand);
}

