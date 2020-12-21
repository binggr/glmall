package com.binggr.glmall.thirdparty.controller;

import com.binggr.common.utils.R;
import com.binggr.glmall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: bing
 * @date: 2020/12/4 14:05
 */
@RestController()
@RequestMapping("/sms")
public class SmsSendController {

    @Autowired
    SmsComponent smsComponent;

    /**
     * 提供给别的服务进行调用的
     * @param phone
     * @param param
     * @return
     */
    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("param")String param){
        smsComponent.sendCode(phone,param);
        return R.ok();
    }
}
