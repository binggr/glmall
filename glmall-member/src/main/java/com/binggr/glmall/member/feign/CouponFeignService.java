package com.binggr.glmall.member.feign;

import com.binggr.common.utils.R;
import com.binggr.glmall.member.vo.SocialUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: bing
 * @date: 2020/10/5 15:15
 */

/**
 * 声明式远程调用
 */
@FeignClient("glmall-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupons();

}
