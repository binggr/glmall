package com.binggr.glmall.search.feign;

import com.binggr.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: bing
 * @date: 2020/12/2 13:59
 */
@FeignClient("glmall-product")
public interface ProductFeignService {

    @GetMapping("/product/attr/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R attrInfo(@PathVariable("attrId") Long attrId);

    @GetMapping("/product/brand/infos")
    public R brandInfos(@RequestParam("brandIds") List<Long> brandIds);
}
