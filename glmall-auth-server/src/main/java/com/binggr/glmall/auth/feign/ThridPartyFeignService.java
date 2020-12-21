package com.binggr.glmall.auth.feign;

import com.binggr.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: bing
 * @date: 2020/12/4 14:16
 */

@FeignClient("glmall-third-party")
public interface ThridPartyFeignService {
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("param")String param);
}
