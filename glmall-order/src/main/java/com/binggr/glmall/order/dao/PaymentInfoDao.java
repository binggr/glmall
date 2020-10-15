package com.binggr.glmall.order.dao;

import com.binggr.glmall.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-05 11:39:11
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
