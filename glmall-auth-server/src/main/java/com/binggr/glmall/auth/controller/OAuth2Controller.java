package com.binggr.glmall.auth.controller;

/**
 * @author: bing
 * @date: 2020/12/5 13:35
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.binggr.glmall.auth.feign.MemberFeignService;
import com.binggr.glmall.auth.vo.SocialUser;
import com.binggr.common.constant.AuthServerConstant;
import com.binggr.common.utils.HttpUtils;
import com.binggr.common.utils.R;
import com.binggr.common.vo.MemberRespVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 社交登录处理
 */
@Slf4j
@Controller
public class OAuth2Controller {

    @Autowired
    MemberFeignService memberFeignService;

    /**
     * 社交登录成功回调
     * @param code
     * @return
     * @throws Exception
     */
    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session, HttpServletResponse servletResponse) throws Exception {
        //1、根据code换取accessToken
        Map<String, String> map = new HashMap<>();
        map.put("client_id","2312607894");
        map.put("client_secret","9d78535f0bfb4112e0114fa83b53d2e6");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.glmall.com/oauth2.0/weibo/success");
        map.put("code",code);

        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", null, null, map);

        //2、处理
        if(response.getStatusLine().getStatusCode()==200){
            //获取到accessToken
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

            //当前是那个社交用户登录
            //当前用户是第一次进入，就自动注册进来(为当前用户生成一个会员账号信息，以后这个社交账号对应指定的会员)
            R r = memberFeignService.oauthLogin(socialUser);
            if(r.getCode() == 0){
                //3、登录成功就返回首页
                MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
                });
                System.out.println("登录成功:用户信息"+data);
                log.info("登录成功：用户信息：{} ",data.toString());
                //1、第一次使用session，命令浏览器保存卡号。JESSIONID这个cookie
                //以后浏览器访问那个网站就会带上这个网站的cookie
                //子域之间 glmall.com auth.glmall.com order.glmall.com
                //发卡的时候（指定域名为父域），即使子系统发的卡，也能让父域直接使用。

                //TODO 1、默认发的令牌。session=dsajkdjl。作用域：当前域（解决子域session共享问题）

                //TODO 2、使用json的序列化方式来序列化对象数据到redis中



                session.setAttribute(AuthServerConstant.LOGIN_USER,data);
//                new Cookie("JESSIONID","data").setDomain();
//                servletResponse.addCookie();
                return "redirect:http://glmall.com";
            }else {
                return "redirect:http://auth.glmall.com/login.html";
            }

        }else {
            return "redirect:http://auth.glmall.com/login.html";
        }

    }

}
