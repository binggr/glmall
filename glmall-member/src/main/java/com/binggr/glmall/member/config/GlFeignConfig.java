package com.binggr.glmall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: bing
 * @date: 2020/12/7 21:48
 */
@Configuration
public class GlFeignConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
       return new RequestInterceptor(){

           @Override
           public void apply(RequestTemplate template) {
                //1、使用RequestContextHolder拿到刚进来的这个请求数据
               ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
               if(attributes!=null){
                   HttpServletRequest request = attributes.getRequest();
                   if(request!=null){
                       //2、同步请求头数据，Cookie
                       String cookie = request.getHeader("Cookie");
                       //给新请求同步了老请求的Cookie
                       template.header("Cookie",cookie);
                   }
               }
           }
       };
    }
}
