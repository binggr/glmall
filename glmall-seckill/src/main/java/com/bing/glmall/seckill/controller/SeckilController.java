package com.bing.glmall.seckill.controller;

import com.bing.glmall.seckill.service.SeckillService;
import com.bing.glmall.seckill.to.SeckillSkuRedisTo;
import com.binggr.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: bing
 * @date: 2020/12/14 10:11
 */
@Slf4j
@Controller
public class SeckilController {

    @Autowired
    SeckillService seckillService;

    /**
     * 返回当前时间可以参与秒杀的商品
     * @return
     */
    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus(){
        log.info("currentSeckillSkus正在执行......");
        List<SeckillSkuRedisTo> vos = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(vos);
    }

    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId) throws InterruptedException {
        Thread.sleep(300);
        SeckillSkuRedisTo skuRedisTo = seckillService.getSkuSeckillInfo(skuId);
        return  R.ok().setData(skuRedisTo);
    }

    //TODO 上架秒杀商品得时候，每一个数据都有过期时间。
    //TODO 秒杀后续的流程，简化了收货地址等信息
    @GetMapping("/kill")
    public String seckill(@RequestParam("killId") String killId,
                     @RequestParam("key") String key,
                     @RequestParam("num") Integer num,
                          Model model){
        //1、判断是否登录 拦截器

        String orderSn = seckillService.kill(killId,key,num);
        model.addAttribute("orderSn",orderSn);

        return "success";
    }
}
