# glmall

#### 介绍
谷粒商城

#### 软件架构

##### 分布式基础概念
微服务、注册中心、配置中心、远程调用、Feign网关

##### 基础开发
SpringBoot2.x、SpringClound、Mybatis-Plus、Vue组件化、阿里云对象存储

##### 环境
Vagrant、Linux、Docker、MySQL、Redis、逆向工程&人人开源

##### 开发规范
1. 数据校验JSR303、全局异常处理、全局统一返回、全局跨域处理
2. 枚举状态、业务状态码、VO与TO与PO划分、逻辑删除
3. Lombok:@Data、@Slf4j

##### 高级开发
SpringCloud 组件

SpringCloud Alibaba-Nacos     (服务配置、注册中心)

SpringCloud Alibaba-Sentinel （流量哨兵，服务降级、熔断）

SpringCloud Alibaba-Senta    （分布式事务低并发）

SpringCloud Alibaba-OSS      （OSS存储）

SpringCloud openFeign        （远程调用）

SpringCloud Gateway          （网关）

Sleuth+Zipkin                （服务链路追踪）


模板引擎thymeleaf

热部署dev-tools

nginx 域名访问、动静分离

接口幂等性 加锁 数据库的乐观锁、悲观锁、分布式事务、令牌机制、分布式锁处理后查看状态，如果处理了就不处理了。

本地事务与分布式事务 并发度不高Seata、并发度高实现最终一直性RabbitMQ

性能与压力测试 JMeter Jvisualvm Jconsole

缓存与分布式锁 缓存击穿（单点）、缓存雪崩（大面积）

缓存加速系统的吞吐量 解决高并发流量 分布式锁 定时任务 同时上架一件商品

解决方案 当大并发进入时加入分布式锁，允许一个流量进入数据库进行查询

ElasticSearch 检索功能，检索数据保存在ElasticSearch中

异步与线程池  异步任务使用线程池 异步编排 CompletableFuture

登录 OAuth2.0 MD5盐值 社交登录 适用于相同域名（SpringSession+ThreadLocal） 单点登录sso 不同域名


#####业务 

商品上架 商品Mapping 上架细节 数据一致性 

商品检索 检索业务分析 检索语句构建 响应数据模型 响应结果封装

商品详情 详情数据 查询详情 sku组合切换 缓存 redis+SpringCache 缓存不一致 缓存的更新、清除 

购物车 需求 临时购物车 登录购物车

订单 订单页面订单构成、订单状态 订单流程 接口幂等性 订单业务 订单创建 库存 支付 收单

秒杀 秒杀业务、秒杀流程、限流  分布式锁Redission 读写锁 信号量

RabbitMQ 在秒杀情况下进行削峰处理 接口调用 应用解耦

支付宝沙箱 进行支付

定时任务与分布式调度 秒杀系统的上架


#####缓存、异步、消息中间件MQ 高并发三大好

#####TODO
WebFlex

ShardingShere 分库分表
