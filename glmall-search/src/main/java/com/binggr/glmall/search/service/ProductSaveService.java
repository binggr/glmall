package com.binggr.glmall.search.service;

import com.binggr.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author: bing
 * @date: 2020/11/17 16:00
 */
public interface ProductSaveService {

    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
