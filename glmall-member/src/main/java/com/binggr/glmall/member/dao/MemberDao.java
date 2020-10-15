package com.binggr.glmall.member.dao;

import com.binggr.glmall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-05 11:19:35
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
