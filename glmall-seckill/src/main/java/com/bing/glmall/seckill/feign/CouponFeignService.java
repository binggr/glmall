package com.bing.glmall.seckill.feign;

import com.binggr.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: bing
 * @date: 2020/12/12 15:13
 */
@FeignClient("glmall-coupon")
public interface CouponFeignService {
    //1、扫描最近需要参与秒杀的活动商品
    @GetMapping("/coupon/seckillsession/latest3DaySession")
    R getLatest3DaySession();

}
