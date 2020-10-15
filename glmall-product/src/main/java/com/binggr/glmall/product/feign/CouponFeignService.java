package com.binggr.glmall.product.feign;

import com.binggr.common.to.SkuReductionTo;
import com.binggr.common.to.SpuBounsTo;
import com.binggr.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: bing
 * @date: 2020/10/13 16:08
 */

@FeignClient("glmall-coupon")
public interface CouponFeignService {

    /**
     * 只要json数据模型是兼容，双方服务无需使用同一个to
     * @param spuBounsTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBounsTo spuBounsTo);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
