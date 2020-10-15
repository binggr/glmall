package com.binggr.glmall.coupon.dao;

import com.binggr.glmall.coupon.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-05 10:53:20
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
