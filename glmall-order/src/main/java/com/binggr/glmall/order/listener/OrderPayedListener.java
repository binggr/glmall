package com.binggr.glmall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.binggr.glmall.order.config.AlipayTemplate;
import com.binggr.glmall.order.service.OrderService;
import com.binggr.glmall.order.vo.PayAsyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: bing
 * @date: 2020/12/11 16:36
 */
@RestController
public class OrderPayedListener {

    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    @PostMapping("/payed/notify")
    public String handleAlipayed(PayAsyncVo vo,HttpServletRequest request) throws AlipayApiException {
        //只要我们收到了支付宝给我们的异步通知，告诉我们订单支付成功，就返回success，支付宝就不再通知
        Map<String, String[]> map = request.getParameterMap();
//        for (String s : map.keySet()) {
//            String value = request.getParameter(s);
//            System.out.println("key=>"+s+"value=>"+value);
//        }
//        System.out.println("支付宝通知：数据=>"+map);

        //验签
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        System.out.println("进入验签");
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(),
                alipayTemplate.getCharset(), alipayTemplate.getSign_type());
        System.out.println(signVerified);
        if(signVerified){
            //签名验证成功
            System.out.println("签名验证成功......");
            String result = orderService.handlePayResult(vo);
            return result;
        }else {
            System.out.println("签名验证失败......");
            return "error";
        }
    }
}
