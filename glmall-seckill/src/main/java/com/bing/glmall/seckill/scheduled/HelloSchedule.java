package com.bing.glmall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author: bing
 * @date: 2020/12/12 14:30
 */

/**
 * 定时任务
 * 1、@EnableScheduling 开启定时任务
 * 2、@Scheduled 开启一个定时任务
 * 3、TaskExecutionAutoConfiguration.class 自动配置类
 *
 * 异步任务
 * 1、@@EnableAsync 开启异步任务功能
 * 2、@Asyns 给希望异步执行的方法上标注
 * 3、TaskExecutionAutoConfiguration.class 自动配置类
 *
 */

@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class HelloSchedule {

    /**
     * 1、Spring中corn 6位组成，秒 分 时 日 月 周 （年除外）
     * 2、在周几的位置。1-7 代表 周一到周日 MON-SUN
     * 3、定时任务不应该阻塞。默认是阻塞的
     *      1）、可以让业务运行以异步的方式，自己提交到线程池 CompletableFuture.runAsync()
     *      2）、支持定时任务线程池.通过设置线程池数量spring.task.scheduling.pool.size=5
     *      3）、让定时任务异步执行
     *          异步任务
     *      解决：使用异步+定时任务来完成定时任务不阻塞的功能
     */
//    @Async
//    @Scheduled(cron = "* * * * * ?")
//    public void hello() throws InterruptedException {
//        log.info("hello");
//        Thread.sleep(3000);
//    }
}
