package com.binggr.glmall.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.binggr.common.vaild.AddGroup;
import com.binggr.common.vaild.UpdateGroup;
import com.binggr.common.vaild.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.binggr.glmall.product.entity.BrandEntity;
import com.binggr.glmall.product.service.BrandService;
import com.binggr.common.utils.PageUtils;
import com.binggr.common.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author binggr
 * @email msdxwu@foxmail.com
 * @date 2020-10-04 23:29:39
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand/*, BindingResult result*/){
//		if(result.hasErrors()){
//		    Map<String,String> map = new HashMap<>();
//		    //1、获取校验的结果
//            result.getFieldErrors().forEach((item)->{
//                //获取到错误的提示
//                String message = item.getDefaultMessage();
//                //获取错误的属性的名字
//                String filed = item.getField();
//                map.put(filed,message);
//            });
//                return R.error(400,"提交数据不成功!").put("data",map);
//        }else {
//            brandService.save(brand);
//        }
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
		brandService.updateDetail(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update/status")
    //@RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated({UpdateStatusGroup.class}) @RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
