package com.binggr.glmall.product.dao;

import com.binggr.glmall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-04 23:29:39
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
