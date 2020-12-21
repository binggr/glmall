package com.binggr.glmall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.binggr.glmall.auth.feign.MemberFeignService;
import com.binggr.glmall.auth.feign.ThridPartyFeignService;
import com.binggr.glmall.auth.vo.UserLoginVo;
import com.binggr.glmall.auth.vo.UserRegistVo;
import com.binggr.common.constant.AuthServerConstant;
import com.binggr.common.exception.BizCodeEnume;
import com.binggr.common.utils.R;
import com.binggr.common.vo.MemberRespVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: bing
 * @date: 2020/12/3 21:48
 */
@Controller
public class LoginController {

    @Autowired
    ThridPartyFeignService thridPartyFeignService;

    @Autowired
    MemberFeignService memberFeignService;

    /**
     * 发送一个请求直接跳转到一个页面
     * SpringMvc viewcontroller:将请求和页面映射过来
     * @return
     */

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone")String phone){
        String redisParam = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if(!StringUtils.isEmpty(redisParam)){
            long l = Long.parseLong(redisParam.split("_")[1]);
            if(System.currentTimeMillis() - l < 60000){
                //60秒内不能再发
                return R.error(BizCodeEnume.SMS_EXCEPTION.getCode(),BizCodeEnume.SMS_EXCEPTION.getMsg());
            }
        }

        //TODO 1、接口防刷

        //2、验证码的二次校验。redis。存key-phone,value-param sms:param:14708285181 -> 1234
        String param = UUID.randomUUID().toString().substring(0, 5);
        //TODO 注意这里


        //redis缓存验证码，防止同一个phone在60秒再次发送验证码
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,param+"_"+System.currentTimeMillis(),10, TimeUnit.MINUTES);


        thridPartyFeignService.sendCode(phone,param);
        return R.ok();
    }

    /**
     * //TODO 重定向携带数据，利用session原理。将数据放在session中。
     *   只要取出这个数据跳到下一个页面，session中的数据就会删掉
     * //TODO 分布式session问题
     * 模拟重定向携带数据 RedirectAttributes redirectAttributes
     * @param vo
     * @param result
     * @param model
     * @param redirectAttributes
     * @return
     */
    //注册成功
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result, Model model, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            //校验
//            result.getFieldErrors().stream().map(fieldError -> {
//                String field = fieldError.getField();
//                String defaultMessage = fieldError.getDefaultMessage();
//                error.put(field, defaultMessage);
//
//            }).collect()
            Map<String, String> errors =
                    result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));
//            model.addAttribute("errors",errors);
            redirectAttributes.addFlashAttribute("errors",errors);
            //Request method "POST" not supported
            //用户注册—>/regist[post]->转发/reg.html 路径映射都是get

            //校验出错，转发到注册页
            return "redirect:http://auth.glmall.com/reg.html";
        }


        //1、校验验证码
        String param = vo.getParam();
        String s = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if(!StringUtils.isEmpty(s)){
            if(param.equals(s.split("_")[0])){
                //验证码通过
                //删除验证码;令牌机制
                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                //真正注册，调用远程服务进行注册
                R r = memberFeignService.regist(vo);
                if(r.getCode()==0){
                    //成功
                    return "redirect:http://auth.glmall.com/login.html";
                }else{
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg",r.getData(new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors",errors);
                    return "redirect:http://auth.glmall.com/reg.html";
                }

            }else {
                Map<String, String> errors = new HashMap<>();
                errors.put("param","验证码错误");
                redirectAttributes.addFlashAttribute("errors",errors);
                //校验出错，转发到注册页
                return "redirect:http://auth.glmall.com/reg.html";
            }
        }else {
            Map<String, String> errors = new HashMap<>();
            errors.put("param","验证码错误");
            redirectAttributes.addFlashAttribute("errors",errors);
            //校验出错，转发到注册页
            return "redirect:http://auth.glmall.com/reg.html";
        }


        //2、注册成功回到首页，回到登录页
    }

    @GetMapping("/login.html")
    public String LoginPage(HttpSession session){
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(attribute==null){
            //没登录
            return "login";
        }else {
            //已登录
            return "redirect:http://glmall.com";
        }


    }


    @PostMapping("/login")
    public String login(UserLoginVo vo,RedirectAttributes redirectAttributes, HttpSession session){
        //远程登录
        R r = memberFeignService.login(vo);
        if(r.getCode()==0){
            MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
            });

            //成功 放入session中
            session.setAttribute(AuthServerConstant.LOGIN_USER,data);
            return "redirect:http://glmall.com";
        }else {
            Map<String,String> errors = new HashMap<>();
            errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.glmall.com/login.html";
        }

    }




}
