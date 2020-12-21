package com.binggr.glmall.product.feign;

import com.binggr.common.to.SkuHasStockVo;
import com.binggr.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author: bing
 * @date: 2020/11/17 14:42
 */
@FeignClient("glmall-ware")
public interface WareFeignService {

    /**
     * 1、R设计加上泛型
     * 2、接口返回为List
     * 3、自己封装解析结果
     *
     * @param skuIds
     * @return
     */

    //查询sku是否有库存
    @PostMapping("/ware/waresku/hasstock")
    R  getSkuHasStock(@RequestBody List<Long> skuIds);
}
