package com.binggr.glmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.binggr.common.utils.PageUtils;
import com.binggr.glmall.order.entity.PaymentInfoEntity;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-05 11:39:11
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

