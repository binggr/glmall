package com.binggr.glmall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.binggr.glmall.product.entity.BrandEntity;
import com.binggr.glmall.product.service.BrandService;
import com.binggr.glmall.product.service.CategoryService;
import jdk.nashorn.internal.ir.BaseNode;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;


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
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

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
