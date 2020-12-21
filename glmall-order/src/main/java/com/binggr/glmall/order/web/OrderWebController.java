package com.binggr.glmall.order.web;

import com.binggr.common.exception.NoStockException;
import com.binggr.glmall.order.service.OrderService;
import com.binggr.glmall.order.vo.OrderConfirmVo;
import com.binggr.glmall.order.vo.OrderSubmitVo;
import com.binggr.glmall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

/**
 * @author: bing
 * @date: 2020/12/7 19:41
 */
@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData",orderConfirmVo);
        return "confirm";
    }

    /**
     * 下单功能
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes){

        try{
            //创建订单、验令牌、验价格、锁库存
            SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);
            if(responseVo.getCode()==0){
                //下单成功来到支付选择页
                model.addAttribute("submitOrderResponseVo",responseVo);
                return "pay";
            }else {
                //下单失败回到订单确认页重新确认订单信息
                String msg = "下单失败:";
                switch (responseVo.getCode()){
                    case 1: msg+="订单信息过期,请刷新再次提交！";break;
                    case 2: msg+="订单商品价格发生变化,请确认后再次提交！";break;
                    case 3: msg+="库存锁定失败,商品库存不足！";break;
                }
                redirectAttributes.addFlashAttribute("msg",msg);
                return "redirect:http://order.glmall.com/toTrade";
            }
        }catch (Exception e){
            if(e instanceof NoStockException){
                String message = ((NoStockException) e).getMessage();
                redirectAttributes.addFlashAttribute("msg",message);
            }
            return "redirect:http://order.glmall.com/toTrade";
        }

    }
}
