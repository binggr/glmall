package com.bing.glmall.seckill.config;

import com.bing.glmall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author: bing
 * @date: 2020/12/12 15:03
 */

@EnableAsync
@EnableScheduling
@Configuration
public class ScheduleConfig {


}
