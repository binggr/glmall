package com.bing.glmall.seckill.scheduled;

import com.bing.glmall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author: bing
 * @date: 2020/12/12 17:01
 */

/**
 * 秒杀商品定时上线
 * 每天网上3点：上架最近三天需要秒杀的商品。
 * 当天00:00:00 - 23:59:59
 * 明天00:00:00 - 23:59:59
 * 后天00:00:00 - 23:59:59
 *
 */
@Slf4j
@Service
public class SeckillSkuScheduled {
    @Autowired
    SeckillService seckillService;

    @Autowired
    RedissonClient redissonClient;

    private final String up_lock = "seckill:upload:lock";

    //TODO 商品的幂等性
    @Scheduled(cron ="0 * 3 * * ?")
    public void uploadSeckillSkuLatest3Days(){
        //1、重复上架无需处理
        log.info("上架秒杀的商品");
        //分布式锁 锁的业务执行完成，状态已经更新完成。释放锁以后，其他人才能拿到最新的状态
        RLock lock = redissonClient.getLock(up_lock);
        lock.lock(10, TimeUnit.SECONDS);
        try{
            seckillService.uploadSecKillSkuLatest3Days();
        }finally {
            lock.unlock();
        }
    }

}
