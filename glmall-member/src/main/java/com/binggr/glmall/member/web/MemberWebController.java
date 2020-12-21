package com.binggr.glmall.member.web;

import com.alibaba.fastjson.JSON;
import com.binggr.common.utils.R;
import com.binggr.glmall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: bing
 * @date: 2020/12/11 13:39
 */
@Controller
public class MemberWebController {

    @Autowired
    OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, Model model){
        //获取支付宝给我们传来的所有数据
        //request 验证签名 如果正确了可以去修改
        //异步方式

        //查出当前登录用户的所有订单列表数据
        Map<String,Object> page = new HashMap<>();
        page.put("page",pageNum.toString());
        R r = orderFeignService.listWithItem(page);
        model.addAttribute("orders",r);
        System.out.println(JSON.toJSONString(r));
        return "orderList";
    }
}

