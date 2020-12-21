package com.binggr.glmall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.binggr.glmall.product.dao.AttrGroupDao;
import com.binggr.glmall.product.entity.BrandEntity;
import com.binggr.glmall.product.service.AttrGroupService;
import com.binggr.glmall.product.service.BrandService;
import com.binggr.glmall.product.service.CategoryService;
import com.binggr.glmall.product.service.SkuSaleAttrValueService;
import com.binggr.glmall.product.vo.SkuItemSaleAttrVo;
import com.binggr.glmall.product.vo.SkuItemVo;
import com.binggr.glmall.product.vo.SpuItemAttrGroupVo;
import jdk.nashorn.internal.ir.BaseNode;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * 文件上传
 * 1、导入依赖
 * 2、写配置
 * 3、使用ossClient 进行操作
 */
@Slf4j
@SpringBootTest
class GlmallProductApplicationTests {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Test
    void testSkuSaleValue(){
        List<SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueService.getSaleAttrsBySpuId(10L);
        System.out.println(saleAttrsBySpuId);
    }

    @Test
    void test(){
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(3L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);

    }

    @Test
    void redisson(){
        System.out.println(redissonClient);
    }

    @Test
    void testStringRedisTemplate(){
        //hello world
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();

        //保存
        stringStringValueOperations.set("hello","world"+ UUID.randomUUID().toString());

        //查询
        String hello = stringStringValueOperations.get("hello");
        System.out.println(hello);

    }

    @Test
    void testFind(){
        Long[] catelogPath = categoryService.findCatelogPath(449L);
        log.info("完整路径：{}", Arrays.asList(catelogPath));
    }

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();

//        brandEntity.setName("小米");
//        brandService.save(brandEntity);

//        brandEntity.setBrandId(1L);
//        brandEntity.setName("小米");
//        brandEntity.setDescript("小米");
//        brandService.updateById(brandEntity);

        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id",1L));
        list.forEach((item)->{
            System.out.println(item);
        });
        System.out.println("成功！");
    }


}
