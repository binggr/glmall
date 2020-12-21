package com.binggr.glmall.order.web;

import com.alipay.api.AlipayApiException;
import com.binggr.glmall.order.config.AlipayTemplate;
import com.binggr.glmall.order.service.OrderService;
import com.binggr.glmall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: bing
 * @date: 2020/12/11 12:05
 */
@Controller
public class PayWebController {

    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    /**
     * 1、将支付页让浏览器展示
     * 2、支付成功后，跳到用户订单列表页
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "/payOrder",produces = "text/html")
    public String payOrder(@RequestParam("orderSn")String orderSn) throws AlipayApiException {
//        PayVo payVo = new PayVo();
//        payVo.setBody();//订单的备注
//        payVo.setOut_trade_no();//订单号
//        payVo.setSubject();//订单的标题
//        payVo.setTotal_amount();//订单的金额
        PayVo payVo = orderService.getOrderPay(orderSn);
        String pay = alipayTemplate.pay(payVo);
        //返回一个页面，将此页面直接返回给浏览器
        return pay;
    }
}
