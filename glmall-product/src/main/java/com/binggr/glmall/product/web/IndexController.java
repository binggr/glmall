package com.binggr.glmall.product.web;

import com.binggr.glmall.product.entity.CategoryEntity;
import com.binggr.glmall.product.service.CategoryService;
import com.binggr.glmall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: bing
 * @date: 2020/11/18 14:57
 */

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redisson;

    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){

        //TODO 查出所有的一级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Category();

        //视图解析器进行拼串 //classpath: templates/+index+ .html
        model.addAttribute("categorys",categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String,List<Catelog2Vo>> getCatalogJson(){
        Map<String, List<Catelog2Vo>> map =  categoryService.getCatalogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        //1、获取一把锁，只要锁的名字一样，就是同一把锁
        RLock mylock = redisson.getLock("mylock");

        //2、加锁

//        mylock.lock();//阻塞式等待，默认加的锁都是30s
        //(1)、锁的自动续期，如果业务超长，运行期间自动给锁续上新的30s，不用担心业务时间长，锁自动过期被删掉
        //(2)、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认在30s后自动删除

        mylock.lock(10, TimeUnit.SECONDS);//10s自动解锁，自动解锁时间一定要大于业务的执行时间
        // 问题 mylock.lock(10, TimeUnit.SECONDS); 在锁时间到了之后，不会自动续期
        //1、如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是我们指定的时间
        //2、如果我们未指定了锁的超时时间，就使用 30 * 10000 【LockWatchdogTimeout看门狗的默认时间】
        // 只要占锁成功，就会启动一个定时任务【重新设置过期时间，新的过期时间就是看门狗的过期时间】,每隔10s都会自动续期，30s
        // internallockleoseTime【看门狗时间】 / 3,10s

        //最佳实战
        //mylock.lock(10, TimeUnit.SECONDS); 省掉整个续期操作，手动解锁
        try{
            System.out.println("加锁成功，执行业务..."+Thread.currentThread().getName());
            Thread.sleep(30000);
        }catch (Exception e){

        }finally {
            //3、解锁
            mylock.unlock();
            System.out.println("释放锁..."+Thread.currentThread().getName());
        }

        return "hello";
    }

    //读写锁，保证一定能读到最新数据，修改期间，写锁是一个排他锁（互斥锁、独享锁），读锁是一个共享锁
    //写锁没释放，读锁必须等待
    //读 + 读，相当于读锁，并发读，只会在redis中记录好，他们会同时加载成功
    //写 + 读，等待写锁释放
    //写 + 读，阻塞方式
    //读 + 写，有读锁，写锁也必须等待
    //只要有写的存在，都必须等待
    @ResponseBody
    @GetMapping("/write")
    public String writeValue(){
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        RLock writeLock = readWriteLock.writeLock();
        String s = "";
        try{
            //1、改数据加写锁
            writeLock.lock();
            s = UUID.randomUUID().toString();
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("writeValue",s);
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            writeLock.unlock();
        }

        return s;
    }

    @ResponseBody
    @GetMapping("/read")
    public String readValue(){
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        RLock readLock = readWriteLock.readLock();
        String s = "";
        readLock.lock();
        try{
            //1、读数据加写锁
            Thread.sleep(30000);
            s = (String) redisTemplate.opsForValue().get("writeValue");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            readLock.unlock();
        }

        return s;
    }

    @ResponseBody
    @GetMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.trySetCount(5);
        door.await();//等待闭锁都完成

        return "放假了...";
    }

    @ResponseBody
    @GetMapping("/gogogo/{id}")
    public String gogogo(@PathVariable Long id){
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.countDown();//计数减一

        return id+"班的人都走了...";
    }

    /**
     * 车库停车
     * 3车位
     */
    //分布式信号量，分布式限流
    @ResponseBody
    @GetMapping("/park")
    public String park() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        boolean b = park.tryAcquire();//获取一个信号，获取一个值，占一个车位
        return "ok=>"+b;
    }
    @ResponseBody
    @GetMapping("/go")
    public String go() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        park.release();//释放一个信号，释放一个车位
        return "ok";
    }


}
