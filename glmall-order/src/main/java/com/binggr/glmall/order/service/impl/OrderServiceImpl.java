package com.binggr.glmall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.binggr.common.exception.NoStockException;
import com.binggr.common.to.SeckillOrderTo;
import com.binggr.common.to.mq.OrderTo;
import com.binggr.common.utils.R;
import com.binggr.common.vo.MemberRespVo;
import com.binggr.glmall.order.constant.OrderConstant;
import com.binggr.glmall.order.entity.OrderItemEntity;
import com.binggr.glmall.order.entity.PaymentInfoEntity;
import com.binggr.glmall.order.enume.OrderStatusEnum;
import com.binggr.glmall.order.feign.CartFeignService;
import com.binggr.glmall.order.feign.MemberFeignService;
import com.binggr.glmall.order.feign.ProductFeignService;
import com.binggr.glmall.order.feign.WmsFeignService;
import com.binggr.glmall.order.interceptor.LoginUserInterceptor;
import com.binggr.glmall.order.service.OrderItemService;
import com.binggr.glmall.order.service.PaymentInfoService;
import com.binggr.glmall.order.to.OrderCreateTo;
import com.binggr.glmall.order.vo.*;
import com.binggr.glmall.order.vo.SubmitOrderResponseVo;
//import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.binggr.common.utils.PageUtils;
import com.binggr.common.utils.Query;

import com.binggr.glmall.order.dao.OrderDao;
import com.binggr.glmall.order.entity.OrderEntity;
import com.binggr.glmall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    ThreadPoolExecutor executor;


    @Autowired
    WmsFeignService wmsFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    PaymentInfoService paymentInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();



        //获取之前的请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            //1、远程查询所有的收货地址
            //每一个线程共享之前请求的数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            orderConfirmVo.setAddress(address);
        }, executor);

        CompletableFuture<Void> getCurrentUserItemFuture = CompletableFuture.runAsync(() -> {
            //2、远程查询购物车所有选中的购物项
            //每一个线程共享之前请求的数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItem();
            orderConfirmVo.setItems(items);
        }, executor).thenRunAsync(()->{
            List<OrderItemVo> items = orderConfirmVo.getItems();
            List<Long> collect = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            //远程调用库存服务
            R skuHasStock = wmsFeignService.getSkuHasStock(collect);
            List<SkuHasStockVo> data = skuHasStock.getData("data", new TypeReference<List<SkuHasStockVo>>() {
            });
            if(data!=null){
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
                orderConfirmVo.setStocks(map);
            }
        },executor);

        //feign在远程调用之前要构造请求，调用很多的拦截器RequestTemplate

        //3、查询用户积分
        Integer integration = memberRespVo.getIntegration();
        orderConfirmVo.setIntegration(integration);

        CompletableFuture.allOf(getAddressFuture,getCurrentUserItemFuture).get();

        //4、其他数据自动计算

        //TODO 5、防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");

        //reidis
        stringRedisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberRespVo.getId(),token,30, TimeUnit.MINUTES);
        //页面
        orderConfirmVo.setOrderToken(token);

        return orderConfirmVo;
    }

    /**
     * //创建订单、验令牌、验价格、锁库存
     * @param vo
     * @return
     */
    //本地事务，在分布式系统，只能控制住自己的回滚，控制不了其他服务的回滚
    //分布式事务： 最大原因：网络原因+分布式机器
    //@Transactional(isolation = Isolation.REPEATABLE_READ) 数据库默认隔离级别
    //REQUIRED（默认） 共用一个事务 子事务会继承父事务所有设置 REQUIRES_NEW 新建一个事务 传播行为

    //如果在同一个Service 类中，子事务设置会完全继承父事务设置 子事务设置失效  失效原因，绕过了代理对象
    // 不同Service 类中，生效
    //timeout 超时时间
//    @GlobalTransactional //高并发不适合
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        confirmVoThreadLocal.set(vo);
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();

        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        responseVo.setCode(0);
        //1、验证令牌 【令牌的对比和删除必须保证原子性】
        //0令牌失败 - 1删除成功
        String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();
        //原子验证令牌和删除令牌
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);
        if(result == 0L){
            //令牌验证失败
            responseVo.setCode(1);
            return responseVo;
        }else{
            //令牌验证成功
            //创建订单、验价格、锁库存
            //1、创建订单、订单项等信息
            OrderCreateTo order = createOrder();
            //2、验价
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if(Math.abs(payAmount.subtract(payPrice).doubleValue())<0.01){
                //1、金额对比
                //...
                //TODO 3、保存订单
                saveOrder(order);
                //4、库存锁定。只要有异常，订单数据回滚。
                // 订单号。所有订单项（skuId，skuName，num）
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> collect = order.getOrderItems().stream().map(item -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(collect);
                //TODO 4、远程处理 锁库存
                //库存成功了，网络原因超时了，订单回滚，库存不回滚

                //为了保证高并发，库存服务自己回滚。可以发消息给库存服务。
                //库存服务本身可以使用自动解锁模式
                R r = wmsFeignService.orderLockStock(lockVo);
                if(r.getCode() == 0){
                    //锁成功了
                    //TODO 5、远程扣减积分
//                    int i = 10/0;//订单回滚，库存不回滚
                    responseVo.setOrder(order.getOrder());
                    //TODO 订单创建成功给MQ发送消息
                    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order.getOrder());
                    return responseVo;
                }else {
                    //锁失败了
                    String msg = (String)r.get("msg");
                    throw  new NoStockException(msg);
                }
            }else{
                //验价失败
                responseVo.setCode(2);
                return responseVo;
            }
        }


//        String redisToken = stringRedisTemplate.opsForValue()
//                .get(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId());
//        if(orderToken!=null && orderToken.equals(redisToken)){
//            //令牌验证通过
//            stringRedisTemplate.delete(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId());
//        }else {
//            //不通过
//
//        }

    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return orderEntity;
    }

    @Override
    public void closeOrder(OrderEntity entity) {
        //查询当前这个订单的最新状态
        System.out.println("进入关单");
        OrderEntity orderEntity = this.getById(entity.getId());
        //关单
        if(orderEntity.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()){
            OrderEntity update = new OrderEntity();
            update.setId(entity.getId());
            update.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(update);
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity,orderTo);
            //发给MQ一个释放库存消息
            //TODO 网络原因
            //TODO 保证消息百分百发出去，每一个消息都可以做好日志记录(给数据库保存每个消息的详细信息)。
            //TODO 定期扫描数据库将失败的消息再发送一遍
            try{
                rabbitTemplate.convertAndSend("order-event-exchange","order.release.other",orderTo);
            }catch (Exception e){
                //TODO 将没发出去的消息进行重新派发。
                //while
            }

        }


    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity order = this.getOrderByOrderSn(orderSn);

        BigDecimal bigDecimal = order.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(bigDecimal.toString());
        payVo.setOut_trade_no(orderSn);

        List<OrderItemEntity> order_sn = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity itemEntity = order_sn.get(0);

        payVo.setSubject(itemEntity.getSkuName());
        payVo.setBody(itemEntity.getSkuAttrsVals());
        return payVo;
    }

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id",memberRespVo.getId()).orderByDesc("id")
        );

        List<OrderEntity> collect = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItemEntities(orderItemEntities);
            return order;
        }).collect(Collectors.toList());

        page.setRecords(collect);

        return new PageUtils(page);
    }

    /**
     * 处理支付宝的支付结果
     * @param vo
     * @return
     */
    @Override
    public String handlePayResult(PayAsyncVo vo) {
        //1、保存交易流水
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setAlipayTradeNo(vo.getTrade_no());
        paymentInfoEntity.setOrderSn(vo.getOut_trade_no());
        paymentInfoEntity.setPaymentStatus(vo.getTrade_status());
        paymentInfoEntity.setCallbackTime(vo.getNotify_time());
        paymentInfoService.save(paymentInfoEntity);
        //2、修改订单状态信息
        if(vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED")){
            //支付成功状态
            String tradeNo = vo.getOut_trade_no();
            System.out.println(tradeNo);
            this.baseMapper.updateOrderStatus(tradeNo,OrderStatusEnum.PAYED.getCode());
        }
        return "success";
    }

    @Override
    public void createSeckillOrder(SeckillOrderTo seckillOrderTo) {
        //TODO 保存订单信息
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(seckillOrderTo.getOrderSn());
        orderEntity.setMemberId(seckillOrderTo.getMemberId());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        BigDecimal multiply = seckillOrderTo.getSeckillPrice().multiply(new BigDecimal("" + seckillOrderTo.getNum()));
        orderEntity.setPayAmount(multiply);
        this.save(orderEntity);

        // TODO 保存订单项信息
        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setOrderSn(seckillOrderTo.getOrderSn());
        itemEntity.setRealAmount(multiply);
        itemEntity.setSkuQuantity(seckillOrderTo.getNum());
        //TODO 获取当前sku详细信息进行设置 productFeignService.getSpuInfoBySkuId()

        orderItemService.save(itemEntity);

    }


    /**
     * 保存订单数据
     * @param order
     */
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);

        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems);

    }

    //创建订单
    private OrderCreateTo createOrder(){
        OrderCreateTo orderCreateTo = new OrderCreateTo();

        //1、生成一个订单号
        String orderSn = IdWorker.getTimeId();

        //创建订单号
        OrderEntity orderEntity = buildOrder(orderSn);
        orderCreateTo.setOrder(orderEntity);

        //2、获取到所有的订单项
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);
        orderCreateTo.setOrderItems(orderItemEntities);

        //3、计算价格相关
        computerPrice(orderEntity,orderItemEntities);

        return  orderCreateTo;
    }

    private void computerPrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {

        BigDecimal total = new BigDecimal("0.0");

        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");

        BigDecimal gift = new BigDecimal("0.0");
        BigDecimal growth = new BigDecimal("0.0");

        //订单的总额，叠加每一个订单项的总额信息
        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            BigDecimal realAmount = orderItemEntity.getRealAmount();
            total = total.add(realAmount);
            coupon = coupon.add(orderItemEntity.getCouponAmount());
            integration = integration.add(orderItemEntity.getIntegrationAmount());
            promotion = promotion.add(orderItemEntity.getPromotionAmount());
            gift = gift.add(new BigDecimal(orderItemEntity.getGiftIntegration().toString()));
            growth = growth.add(new BigDecimal(orderItemEntity.getGiftGrowth().toString()));
        }
        //1、订单价格相关
        orderEntity.setTotalAmount(total);
        //应付总额
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setCouponAmount(coupon);
        orderEntity.setIntegrationAmount(integration);

        //设置积分信息
        orderEntity.setIntegration(gift.intValue());
        orderEntity.setGrowth(growth.intValue());

        //设置订单的相关状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setDeleteStatus(0);//未删除

    }


    //创建订单项
    private OrderEntity buildOrder(String orderSn) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberId(memberRespVo.getId());

        OrderSubmitVo orderSubmitVo = confirmVoThreadLocal.get();
        //远程获取收货地址信息
        R fare = wmsFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareResp = fare.getData(new TypeReference<FareVo>(){});
        //设置运费信息
        orderEntity.setFreightAmount(fareResp.getFare());
        //设置收货人信息
        orderEntity.setReceiverCity(fareResp.getAddressVo().getCity());
        orderEntity.setReceiverName(fareResp.getAddressVo().getName());
        orderEntity.setReceiverPhone(fareResp.getAddressVo().getPhone());
        orderEntity.setReceiverPostCode(fareResp.getAddressVo().getPostCode());
        orderEntity.setReceiverProvince(fareResp.getAddressVo().getProvince());
        orderEntity.setReceiverRegion(fareResp.getAddressVo().getRegion());
        return orderEntity;
    }

    /**
     * 构建所有订单项数据
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        //最后确定每个购物项价格
        List<OrderItemVo> currentUserCartItem = cartFeignService.getCurrentUserCartItem();
        if(currentUserCartItem!=null && currentUserCartItem.size()>0){
            List<OrderItemEntity> collect = currentUserCartItem.stream().map(cartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    /**
     * 构建某一个订单项
     * @param cartItem
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        //1、订单信息，订单号 v

        //2、商品的spu信息 v
        Long skuId = cartItem.getSkuId();
        //远程查询Spu信息
        R info = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = info.getData(new TypeReference<SpuInfoVo>(){});
        itemEntity.setSpuId(data.getId());
        itemEntity.setSpuBrand(data.getBrandId().toString());
        itemEntity.setSpuName(data.getSpuName());
        itemEntity.setCategoryId(data.getCatalogId());

        //3、商品的sku信息 v
        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPrice(cartItem.getPrice());
        itemEntity.setSkuPic(cartItem.getImage());
        String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
        itemEntity.setSkuAttrsVals(skuAttr);
        itemEntity.setSkuQuantity(cartItem.getCount());
        //4、优惠信息[忽略]

        //5、积分信息
        itemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());

        //6、订单项的价格信息
        itemEntity.setPromotionAmount(new BigDecimal("0"));
        itemEntity.setCouponAmount(new BigDecimal("0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0"));
        //当前订单项的实际金额.总额，各种优惠
        BigDecimal origin = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        //总额减去各种优惠
        BigDecimal real = origin.subtract(itemEntity.getCouponAmount()).subtract(itemEntity.getPromotionAmount()).subtract(itemEntity.getIntegrationAmount());

        itemEntity.setRealAmount(real);

        return itemEntity;
    }

}