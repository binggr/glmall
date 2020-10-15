package com.binggr.glmall.coupon.dao;

import com.binggr.glmall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-05 10:53:20
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
