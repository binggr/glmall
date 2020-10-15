package com.binggr.glmall.product.dao;

import com.binggr.glmall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-04 23:29:40
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
