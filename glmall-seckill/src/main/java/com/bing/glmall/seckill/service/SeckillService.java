package com.bing.glmall.seckill.service;

import com.bing.glmall.seckill.to.SeckillSkuRedisTo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: bing
 * @date: 2020/12/12 15:09
 */
public interface SeckillService {

    void uploadSecKillSkuLatest3Days();

    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    SeckillSkuRedisTo getSkuSeckillInfo(Long skuId);

    String kill(String killId, String key, Integer num);
}
