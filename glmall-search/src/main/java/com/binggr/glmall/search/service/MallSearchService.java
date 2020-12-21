package com.binggr.glmall.search.service;

import com.binggr.glmall.search.vo.SearchParam;
import com.binggr.glmall.search.vo.SearchResult;

/**
 * @author: bing
 * @date: 2020/11/26 14:50
 */
public interface MallSearchService {

    /**
     *
     * @param param 检索的所有参数，返回所有的检索结果
     * @return 返回检索的结果，里面包含页面需要的所有信息
     */
    SearchResult search(SearchParam param);
}
