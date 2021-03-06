package com.binggr.glmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.binggr.common.utils.PageUtils;
import com.binggr.glmall.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-05 11:19:35
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

