package com.binggr.glmall.search.controller;

import com.binggr.glmall.search.service.MallSearchService;
import com.binggr.glmall.search.vo.SearchParam;
import com.binggr.glmall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;


/**
 * @author: bing
 * @date: 2020/11/26 13:50
 */

@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    /**
     * 自动将页面提交过来的所有请求查询参数封装成指定的对象
     * @param param
     * @return
     */
    @GetMapping("/list.html")
    public String lisPage(Model model, SearchParam param, HttpServletRequest request){

        String queryString = request.getQueryString();

        param.set_queryString(queryString);

        //1、根据传递来的页面查询参数，去es中检索商品
        SearchResult result = mallSearchService.search(param);

        model.addAttribute("result",result);
        return "list";
    }




}
