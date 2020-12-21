package com.binggr.glmall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: bing
 * @date: 2020/12/3 22:46
 */
@Configuration
public class GlmallWebConfig implements WebMvcConfigurer {

    /**
     *  @GetMapping("/login.html")
     *     public String loginPage(){
     *         return "login";
     *     }
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
    }
}
