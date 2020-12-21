package com.binggr.glmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.binggr.common.utils.PageUtils;
import com.binggr.glmall.member.entity.MemberEntity;
import com.binggr.glmall.member.exception.PhoneExistException;
import com.binggr.glmall.member.exception.UsernameExistException;
import com.binggr.glmall.member.vo.MemberLoginVo;
import com.binggr.glmall.member.vo.MemberRegistVo;
import com.binggr.glmall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-05 11:19:35
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVo memberRegistVo);

    void checkPhoneUnique(String phone) throws PhoneExistException;

    void checkUsernameUnique(String username) throws UsernameExistException;

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUser socialUser) throws Exception;
}

