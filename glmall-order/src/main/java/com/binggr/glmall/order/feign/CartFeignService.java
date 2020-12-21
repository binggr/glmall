package com.binggr.glmall.order.feign;

import com.binggr.glmall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author: bing
 * @date: 2020/12/7 21:20
 */
@FeignClient("glmall-cart")
public interface CartFeignService {
    @GetMapping("/currentUserCartItem")
    List<OrderItemVo> getCurrentUserCartItem();
}
