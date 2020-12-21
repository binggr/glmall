package com.bing.glmall.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.bing.glmall.seckill.feign.CouponFeignService;
import com.bing.glmall.seckill.feign.ProductFeignService;
import com.bing.glmall.seckill.interceptor.LoginUserInterceptor;
import com.bing.glmall.seckill.service.SeckillService;
import com.binggr.common.to.SeckillOrderTo;
import com.bing.glmall.seckill.to.SeckillSkuRedisTo;
import com.bing.glmall.seckill.vo.SeckillSessionWithSkusVo;
import com.bing.glmall.seckill.vo.SkuInfoVo;
import com.binggr.common.utils.R;
import com.binggr.common.vo.MemberRespVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * @author: bing
 * @date: 2020/12/12 15:09
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RedissonClient redissonClient;

    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    private final String SKU_CACHE_PREFIX = "seckill:skus:";
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";//+商品随机码

    @Override
    public void uploadSecKillSkuLatest3Days() {
        //1、去扫描需要参加秒杀的商品
        R session = couponFeignService.getLatest3DaySession();
        if (session.getCode() == 0) {
            //上架商品
            List<SeckillSessionWithSkusVo> sessionData = session.getData(new TypeReference<List<SeckillSessionWithSkusVo>>() {
            });
            //缓存到Redis中
            //1、缓存活动的信息
            saveSessionInfos(sessionData);
            //2、缓存活动的关联商品信息
            saveSessionSkuInfos(sessionData);

        }

    }

    public List<SeckillSkuRedisTo> blockHandler(BlockException e){
        log.error("getCurrentSeckillSkusResource 被限流了");
        return null;
    }

    /**
     * blockHandler函数会在方法被限流、降级、系统保护的时候调用，而fallback函数会针对所有类型的异常
     *
     * 获取当前可以参与秒杀的信息
     * @return
     */
    @SentinelResource(value = "getCurrentSeckillSkusResource",blockHandler = "blockHandler")
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        //1、确定当前时间属于哪个秒杀场次。
        long time = new Date().getTime();
        try(Entry entry = SphU.entry("seckillSkus")){
            Set<String> keys = stringRedisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
            for (String key : keys) {
                String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
                String[] s = replace.split("_");
                Long start = Long.parseLong(s[0]);
                long end = Long.parseLong(s[1]);
                if (time >= start && time <= end) {
                    //2、获取这个秒杀场次需要的所有商品信息
                    List<String> range = stringRedisTemplate.opsForList().range(key, -100, 100);
                    BoundHashOperations<String, String, Object> hashOps = stringRedisTemplate.boundHashOps(SKU_CACHE_PREFIX);
                    List<Object> list = hashOps.multiGet(range);
                    if (list != null) {
                        List<SeckillSkuRedisTo> collect = list.stream().map(item -> {
                            SeckillSkuRedisTo skuRedisTo = JSON.parseObject((String) item, SeckillSkuRedisTo.class);
                            //skuRedisTo.setRandomCode(null); 当前秒杀开始了就需要随机码
                            return skuRedisTo;
                        }).collect(Collectors.toList());

                        return collect;
                    }
                    break;
                }
            }
        }catch (BlockException e){
            log.error("资源被限流,{}",e.getMessage());
        }
        return null;
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        //1、找到所有需要参与秒杀的商品的key
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKU_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        String regex = "\\d_" + skuId;
        if (keys != null) {
            for (String key : keys) {
                //2_18
                if (Pattern.matches(regex, key)) {
                    String json = hashOps.get(key);
                    SeckillSkuRedisTo skuRedisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
                    //随机码
                    long current = new Date().getTime();
                    Long startTime = skuRedisTo.getStartTime();
                    Long endTime = skuRedisTo.getEndTime();
                    if (current >= startTime && current < endTime) {
                    } else {
                        skuRedisTo.setRandomCode(null);
                    }

                    return skuRedisTo;
                }
            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) {
        MemberRespVo respVo = LoginUserInterceptor.loginUser.get();

        long s1 = System.currentTimeMillis();
        //1、获取当前秒杀商品得合法性
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKU_CACHE_PREFIX);

        String s = hashOps.get(killId);
        //获取redis中存储信息
        if (StringUtils.isEmpty(s)) {
            return null;
        } else {
            SeckillSkuRedisTo redis = JSON.parseObject(s, SeckillSkuRedisTo.class);
            //1、检验时间合法性
            long time = new Date().getTime();
            Long startTime = redis.getStartTime();
            Long endTime = redis.getEndTime();
            long ttl = endTime - startTime;

            if (time >= startTime && time <= endTime) {
                //2、校验随机码和商品ID
                String randomCode = redis.getRandomCode();
                String skuId = redis.getPromotionSessionId() + "_" + redis.getSkuId();
                if (randomCode.equals(key) && skuId.equals(killId)) {
                    //3、验证购物的数量是否合理
                    if (num <= redis.getSeckillLimit()) {
                        //4、验证这个人是否购买过。幂等性；如果秒杀成功就去redis占一个位。userId_sessionId_skuId
                        //SETNX
                        String redisKey = respVo.getId() + "_" + skuId;
                        //自动过期
                        Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                        if (aBoolean) {
                            //占位成功说明重来没有买过
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                            boolean b = semaphore.tryAcquire(num);
                            if (b) {
                                //秒杀成功;
                                //快速下单，发送MQ消息 10ms
                                String timeId = IdWorker.getTimeId();
                                SeckillOrderTo seckillOrderTo = new SeckillOrderTo();
                                seckillOrderTo.setOrderSn(timeId);
                                seckillOrderTo.setMemberId(respVo.getId());
                                seckillOrderTo.setNum(num);
                                seckillOrderTo.setPromotionSessionId(redis.getPromotionSessionId());
                                seckillOrderTo.setSkuId(redis.getSkuId());
                                seckillOrderTo.setSeckillPrice(redis.getSeckillPrice());
                                rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", seckillOrderTo);
                                long s2 = System.currentTimeMillis();
                                log.info("耗时...",(s2-s1));
                                return timeId;
                            }
                            return null;
                        } else {
                            //说明已经买过
                            return null;
                        }
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }

        }

        return null;
    }

    private void saveSessionInfos(List<SeckillSessionWithSkusVo> sessionWithSkusVo) {
        sessionWithSkusVo.stream().forEach(session -> {
            Long startTime = session.getStartTime().getTime();
            Long endTime = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;
            Boolean hasKey = stringRedisTemplate.hasKey(key);
            if (!hasKey) {
                List<String> collect = session.getRelationEntities().stream().map(item -> item.getPromotionSessionId().toString() + "_" + item.getSkuId().toString()).collect(Collectors.toList());
                //缓存活动信息
                stringRedisTemplate.opsForList().leftPushAll(key, collect);
            }
        });
    }

    private void saveSessionSkuInfos(List<SeckillSessionWithSkusVo> sessionWithSkusVo) {
        sessionWithSkusVo.stream().forEach(session -> {
            //准备hash操作
            BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(SKU_CACHE_PREFIX);
            session.getRelationEntities().forEach(seckillSkuVo -> {
                //4、商品的随机码
                String token = UUID.randomUUID().toString().replace("-", "");

                if (!ops.hasKey(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString())) {
                    //缓存商品
                    SeckillSkuRedisTo skuRedisTo = new SeckillSkuRedisTo();
                    //1、sku的基本数据
                    R skuInfo = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                    if (skuInfo.getCode() == 0) {
                        SkuInfoVo info = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        skuRedisTo.setSkuInfoVo(info);
                    }

                    //2、sku秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo, skuRedisTo);

                    //3、设置当前商品的开始结束时间
                    skuRedisTo.setStartTime(session.getStartTime().getTime());
                    skuRedisTo.setEndTime(session.getEndTime().getTime());

                    //4、商品的随机码
                    skuRedisTo.setRandomCode(token);

                    String s = JSON.toJSONString(skuRedisTo);
                    ops.put(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString(), s);

                    //如果当前这个商品场次的商品已经上架就不用上架
                    Boolean hasKey = stringRedisTemplate.hasKey(SKU_STOCK_SEMAPHORE + token);
                    //5、使用库存作为分布式的信号量 限流：
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    //商品可以秒杀的数量作为信号量
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                }

            });
        });
    }

}
