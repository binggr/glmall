package com.binggr.glmall.member.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.fastjson.JSON;
import com.binggr.common.exception.BizCodeEnume;
import com.binggr.common.utils.R;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: bing
 * @date: 2020/12/15 10:46
 * sentinel 1.8 WebCallbackManger 被删除
 * WebCallbackManager被删除
 * 只需要创建 BlockExceptionHandler 接口实现类即可实现自动配置
 */
@Configuration
public class SeckillSentinelConfig {
    @Bean
    public BlockExceptionHandler sentinelBlockExceptionHandler() {
        R error = R.error(BizCodeEnume.TO_MANY_REQUEST.getCode(), BizCodeEnume.TO_MANY_REQUEST.getMsg());
        return ((request, response, e) -> {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(JSON.toJSONString(error));
        });
    }
}
