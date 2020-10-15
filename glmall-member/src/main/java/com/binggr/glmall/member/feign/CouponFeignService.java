package com.binggr.glmall.member.feign;

import com.binggr.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
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
