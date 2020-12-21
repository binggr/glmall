package com.binggr.glmall.member.feign;

import com.binggr.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author: bing
 * @date: 2020/12/11 14:37
 */
@FeignClient("glmall-order")
public interface OrderFeignService {

    @RequestMapping("/order/order/listWithItem")
    R listWithItem(@RequestBody Map<String, Object> params);
}
