package com.binggr.glmall.auth.feign;

import com.binggr.glmall.auth.vo.SocialUser;
import com.binggr.glmall.auth.vo.UserLoginVo;
import com.binggr.glmall.auth.vo.UserRegistVo;
import com.binggr.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: bing
 * @date: 2020/12/4 19:55
 */
@FeignClient("glmall-member")
public interface MemberFeignService {
    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVo memberRegistVo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth2/login")
    R oauthLogin(@RequestBody SocialUser socialUser) throws Exception;

}
