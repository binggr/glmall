package com.binggr.glmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.binggr.common.exception.NoStockException;
import com.binggr.common.to.mq.OrderTo;
import com.binggr.common.to.mq.StockDetailTo;
import com.binggr.common.to.mq.StockLockedTo;
import com.binggr.common.utils.R;
import com.binggr.glmall.ware.entity.WareOrderTaskDetailEntity;
import com.binggr.glmall.ware.entity.WareOrderTaskEntity;
import com.binggr.glmall.ware.feign.OrderFeignService;
import com.binggr.glmall.ware.feign.ProductFeignService;
import com.binggr.glmall.ware.service.WareOrderTaskDetailService;
import com.binggr.glmall.ware.service.WareOrderTaskService;
import com.binggr.glmall.ware.vo.OrderItemVo;
import com.binggr.glmall.ware.vo.OrderVo;
import com.binggr.glmall.ware.vo.SkuHasStockVo;
import com.binggr.glmall.ware.vo.WareSkuLockVo;
import com.rabbitmq.client.Channel;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.binggr.common.utils.PageUtils;
import com.binggr.common.utils.Query;

import com.binggr.glmall.ware.dao.WareSkuDao;
import com.binggr.glmall.ware.entity.WareSkuEntity;
import com.binggr.glmall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderFeignService orderFeignService;

    /**
     * 解锁库存
     * @param skuId
     * @param wareId
     * @param num
     * @param taskDetailId
     */
    private void unlockStock(Long skuId,Long wareId,Integer num,Long taskDetailId){
        //库存解锁
        wareSkuDao.unlockStock(skuId,wareId,num);
        //更新库存工作单状态
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(taskDetailId);
        entity.setLockStatus(2);//已解锁
        wareOrderTaskDetailService.updateById(entity);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId:
         * wareId:
         */
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skdId = (String) params.get("skdId");
        if(!StringUtils.isEmpty(skdId)){
            queryWrapper.eq("sku_id",skdId);
        }

        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }


        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1、判断如果没有这个库存记录，新增
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id",skuId).eq("ware_id",wareId));
        if(entities == null || entities.size() == 0){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //TODO 远程查询sku名字,如果失败整个事务无需回滚
            //1、自己catch异常
            try{
                R info = productFeignService.info(skuId);
                Map<String,Object> data = (Map<String,Object>) info.get("skuInfo");
                if(info.getCode() == 0){
                    wareSkuEntity.setSkuName((String)data.get("skuName"));
                }
            }catch (Exception e){

            }
            //TODO 让异常出现不回滚

        wareSkuDao.insert(wareSkuEntity);
        }else {
             wareSkuDao.addStock(skuId,wareId,skuNum);
        }

        }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            //查询当前sku的总库存量
            Long count = baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
//            if(count>0){
//                vo.setHasStock(true);
//            }else{
//                vo.setHasStock(true);
//            }
            vo.setHasStock(count == null ? false : count > 0);
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 为某个订单锁定库存
     *  @Transactional(rollbackFor = NoStockException.class)
     *  默认只要运行时异常都会回滚
     *
     *  库存解锁的场景
     *  1）、下订单成功，订单过期没有支付被系统自动取消，被用户手动取消。都要解锁库存。
     *
     *  2）、下订单成功，库存锁定成功，接下来的业务调用失败，导致库存回滚。
     *       之前锁定的库存就要自动解锁。
     *
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        /**
         * 保存库存工作单的详情
         */
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(taskEntity);

        //1、按照下单收货地址，找一个就近仓库，锁定库存

        //2、找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();

        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //查询这个商品哪里有库存
            List<Long> wareIds = wareSkuDao.ListWareHasSkuStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());


        Boolean alltock = true;
        //2、锁定库存
        for (SkuWareHasStock skuWareHasStock : collect) {
            Boolean skuStocked = false;
            Long skuId = skuWareHasStock.getSkuId();
            List<Long> wareIds = skuWareHasStock.getWareId();
            if(wareIds == null || wareIds.size()==0){
                //没有任何仓库有这个库存
                throw new NoStockException(skuId);
            }
            //1、如果每一个商品都锁定成功，将当前商品锁定了几件的工作单记录，发送给MQ
            //2、锁定失败。前面保存的工作单信息就回滚了。
            // 即使要解锁记录，由于去数据库查不到id，所以就不用解锁
            for (Long wareId : wareIds) {
                //成功就返回1，否则是0
               Long count =   wareSkuDao.lockSkuStock(skuId,wareId,skuWareHasStock.getNum());
               if(count == 1){
                    //锁成功了
                    skuStocked = true;
                    //TODO 保存订单细节 告诉MQ 库存锁定成功
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity(null, skuId, "", skuWareHasStock.getNum(), taskEntity.getId(), wareId, 1);
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setId(taskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity,stockDetailTo);
                    //只发id不行，防止回滚后找不到数据
                    stockLockedTo.setDetail(stockDetailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",stockLockedTo);
                    break;
               }else {
                   //当前仓库锁失败了,重试下一个仓库
               }
            }
            if(skuStocked==false){
                //当前商品所有仓库都没锁住
                throw new NoStockException(skuId);
            }
        }

        //3、肯定全部都是锁成功的
        return true;
    }

    @Override
    public void unlockStock(StockLockedTo to) {
        StockDetailTo detail = to.getDetail();
        Long detailId = detail.getId();
        //解锁
        //1、查询数据库关于这个订单的锁定库存信息
        //有：证明库存锁定成功了
        //  解锁：订单情况
        //          1、没有这个订单。必须解锁
        //          2、有这个订单。订单状态: 已取消 解锁  没取消 不能解锁库存
        //没有：库存锁定失败了，库存回滚了。这种情况无需解锁
        WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(detailId);
        if(byId!=null){
            //解锁
            Long id = to.getId();//库存工作单的Id
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(id);
            String orderSn = taskEntity.getOrderSn();//根据订单号查询订单的状态
            //远程查询订单情况
            R r = orderFeignService.getOrderStatus(orderSn);
            if(r.getCode()==0){
                //订单数据返回成功
                OrderVo data = r.getData(new TypeReference<OrderVo>(){});
                if(data==null || data.getStatus()==4){
                    //订单不存在
                    //订单被取消了，解锁库存
                    if(byId.getLockStatus()==1){
                        //当前工作单已锁定，未解锁
                        unlockStock(detail.getSkuId(),detail.getWareId(),detail.getSkuNum(),detailId);
                    }
                }
            }else {
                //消息拒绝重新放到队列里面，让别人继续消费解锁。
                throw  new RuntimeException("远程服务失败");
            }

        }else {
            //无需解锁
        }
    }

    //防止订单服务卡顿，导致订单状态消息一直改不了，库存消息优先到期，查订单状态为新建状态，库存不进行锁定
    //导致卡顿的订单，永远不能解锁库存
    @Transactional
    @Override
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        //查一下最新库存解锁状态，防止重复解锁
        WareOrderTaskEntity entity =  wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        Long id = entity.getId();
        //按照工作单找到所有 没有解锁的库存进行解锁
        List<WareOrderTaskDetailEntity> list = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", id)
                .eq("lock_status", 1));
        //Long skuId,Long wareId,Integer num,Long taskDetailId
        for (WareOrderTaskDetailEntity taskDetailEntity : list) {
            unlockStock(taskDetailEntity.getSkuId(),taskDetailEntity.getWareId(),taskDetailEntity.getSkuNum(),taskDetailEntity.getId());
        }

    }

    @Data
    class SkuWareHasStock{
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

}