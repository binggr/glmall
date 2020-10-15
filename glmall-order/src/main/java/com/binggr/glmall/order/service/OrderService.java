package com.binggr.glmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.binggr.common.utils.PageUtils;
import com.binggr.glmall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-05 11:39:11
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

