package com.binggr.glmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.binggr.common.utils.PageUtils;
import com.binggr.glmall.member.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息
 *
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-05 11:19:35
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

