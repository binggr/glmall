package com.binggr.glmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.binggr.common.utils.PageUtils;
import com.binggr.glmall.product.entity.CommentReplayEntity;

import java.util.Map;

/**
 * 商品评价回复关系
 *
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-04 23:29:40
 */
public interface CommentReplayService extends IService<CommentReplayEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

