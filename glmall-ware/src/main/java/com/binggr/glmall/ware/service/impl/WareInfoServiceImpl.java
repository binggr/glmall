package com.binggr.glmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.binggr.common.utils.R;
import com.binggr.glmall.ware.feign.MemberFeignService;
import com.binggr.glmall.ware.vo.FareResponseVo;
import com.binggr.glmall.ware.vo.MemberAddressVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.binggr.common.utils.PageUtils;
import com.binggr.common.utils.Query;

import com.binggr.glmall.ware.dao.WareInfoDao;
import com.binggr.glmall.ware.entity.WareInfoEntity;
import com.binggr.glmall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.eq("id",key)
                    .or().like("name",key)
                    .or().like("address",key)
                    .or().like("areacode",key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 根据用户的收货地址计算运费
     * @param addrId
     * @return
     */
    @Override
    public FareResponseVo getFare(Long addrId) {

        //远程查询运费
        R info = memberFeignService.addrInfo(addrId);
        MemberAddressVo data = info.getData("memberReceiveAddress",new TypeReference<MemberAddressVo>() {
        });

        if(data!=null){
            // TODO 可以结合第三方物流接口计算
            FareResponseVo fareResponseVo = new FareResponseVo();
            String phone = data.getPhone();
            String substring = phone.substring(phone.length() - 1, phone.length());
            BigDecimal decimal = new BigDecimal(substring);
            fareResponseVo.setAddressVo(data);
            fareResponseVo.setFare(decimal);

            return fareResponseVo;
        }

        return null;
    }

}