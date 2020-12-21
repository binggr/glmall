package com.binggr.glmall.product.feign;

import com.binggr.common.to.es.SkuEsModel;
import com.binggr.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author: bing
 * @date: 2020/11/17 17:14
 */
@FeignClient("glmall-search")
public interface SearchFeignService {

    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);

}
