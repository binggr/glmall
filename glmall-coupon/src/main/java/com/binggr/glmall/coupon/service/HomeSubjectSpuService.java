package com.binggr.glmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.binggr.common.utils.PageUtils;
import com.binggr.glmall.coupon.entity.HomeSubjectSpuEntity;

import java.util.Map;

/**
 * 专题商品
 *
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-05 10:53:20
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

