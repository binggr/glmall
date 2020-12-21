package com.binggr.glmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.binggr.common.utils.PageUtils;
import com.binggr.common.utils.Query;
import com.binggr.glmall.product.dao.CategoryDao;
import com.binggr.glmall.product.entity.CategoryEntity;
import com.binggr.glmall.product.service.CategoryBrandRelationService;
import com.binggr.glmall.product.service.CategoryService;
import com.binggr.glmall.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {

        //1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //2、组装成父子的树形结构
        //(1)找到所有的一级分类
        List<CategoryEntity> levelMenus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return levelMenus;
    }

    //递归查找当前菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //1、找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //2、菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1、检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);

        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有属性
     * @CacheEvict缓存失效模式
     * 1、@Caching 同时进行多种缓存操作
     * 2、指定删除某个分区的所有数据 @CacheEvict(value = {"category"},allEntries = true)
     * 3、存储同一类型的数据，可以指定成同一个分区。分区名默认为缓存的前缀
     * @param category
     */
//    @CacheEvict(value = {"category"},key = "'getLevel1Category'")
//    @Caching(evict = {
//            @CacheEvict(value = {"category"},key = "'getLevel1Category'"),
//            @CacheEvict(value = {"category"},key = "'getCatalogJson'")
//    })
    @CacheEvict(value = {"category"},allEntries = true)
    @Transactional
    @Override
    public void updateCasecade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());

        //同时修改缓存中的数据
        //redis.del("catalogJSON") 等待下次主动查询进行更新
    }

    //1、每一个需要缓存的数据，我们都来指定要放在哪个名字的缓存【缓存的分区（按照业务类型区分）】
    //2、@Cacheable({"category"})
    //  代表当前方法的结果需要缓存，如果缓存中有，方法不调用
    //  如果方法中没有，会调用方法，最后将方法结果放入缓存中
    //3、默认行为
    // 1、如果缓存有，方法不会调用
    // 2、key默认自动生成，缓存名字：SimpleKey
    // 3、缓存value的值，默认使用jdk序列化机制，将序列化的数据存到redis
    //自定义
    // 1、指定生成的缓存使用的key key属性指定，接受一个SpEL
    // 2、指定缓存的数据的存活时间 配置文件修改ttl
    // 3、将数据保存为JSON格式  缓存配置

    @Cacheable(value = {"category"},key = "#root.method.name",sync = true) //代表当前方法结果需要缓存，如果缓存中有，方法不用调用，如果缓存中没有，会调用方法，将方法结果放入缓存
    @Override
    public List<CategoryEntity> getLevel1Category() {
        System.out.println("getLevel1Categorys...");
        List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return entities;
    }


    @Cacheable(value = {"category"},key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        System.out.println("查询了数据库！");

        /**
         * 1、将数据库多次查询变为一次
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //1、查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //2、封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类，查到这个一级类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1、找当前分类下的二级分类的三级分类封装成vo
                    List<CategoryEntity> lv3Catalog = getParent_cid(selectList, l2.getCatId());
                    if (lv3Catalog != null) {
                        List<Catelog2Vo.Catalog3Vo> collect = lv3Catalog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catelog2Vo.Catalog3Vo catalog3Vo = new Catelog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));


        return parent_cid;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        //给缓存中放json字符串，拿出的Json字符串，还需转为我们能用的字符串才行【序列化与反序列化过程】

        /**
         * 1、空结果缓存，解决缓存穿透 判断数据是否包含，返回空
         * 2、设置过期时间（+随机时间），解决缓存雪崩
         * 3、加锁，解决缓存击穿问题
         */

        //1、加入缓存逻辑，缓存中存的数据是json字符串
        //JSON是跨语言、跨平台兼容的
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            System.out.println("缓存不命中，将要查询数据库！");
            //2、缓存中没有，查询数据库
            Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithRedisLock();
            return catalogJsonFromDb;
        }
        System.out.println("缓存命中！直接返回！");
        //转为我们指定的对象
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });

        return result;
    }

    /**
     * 缓存里面的数据如何和数据库保持一致
     * 缓存数据一致性
     * 1）、双写模式 脏数据 加锁 过期时间
     * 2）、失效模式 加读写锁
     * Canal 更新缓存 大数据解决数据异构
     *
     * 此系统缓存一致性解决方案
     * 1、缓存所有的数据都有过期时间，数据过期下一查询触发主动更新
     * 2、读写数据时，加上分布式的读写锁
     *
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        //1、锁的名字。锁的粒度，越细越快
        //锁的粒度 具体缓存的是某个数据，11-号商品： product-11-lock
        RLock lock = redisson.getLock("catalogJson-lock");
        lock.lock();

        Map<String, List<Catelog2Vo>> dataFromDbToRedis;
        try {
            dataFromDbToRedis = getDataFromDbToRedis();
        } finally {
            lock.unlock();
        }

        return dataFromDbToRedis;

    }

    //数据库查询并封装分类数据 Redis锁
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        //1、占分布式锁，去redis占坑
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功！");
            //加锁成功,执行业务
            //2、设置过期时间,与加锁是同步的、原子的，防止死锁
            //redisTemplate.expire("lock",30,TimeUnit.SECONDS); //在之前设置
            Map<String, List<Catelog2Vo>> dataFromDbToRedis;
            try {
                dataFromDbToRedis = getDataFromDbToRedis();
            } finally {
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                //删除锁原子删除
                Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class)
                        , Arrays.asList("lock"), uuid);
            }

            //获取值对比+对比成功删除=原子操作 lua脚本解锁
//            String lockValue = redisTemplate.opsForValue().get("lock");
//            if(uuid.equals(lockValue)){
//                //删除我自己的锁
//                redisTemplate.delete("lock");//删除锁
//            }

            return dataFromDbToRedis;
        } else {
            //加锁失败，，，重试、、、synchronized
            //TODO 休眠100毫秒重试
            System.out.println("获取分布式锁失败！等待重试...");
            try {
                Thread.sleep(200);
            } catch (Exception e) {

            }
            return getCatalogJsonFromDbWithRedisLock();//自旋的方式
        }

    }

    private Map<String, List<Catelog2Vo>> getDataFromDbToRedis() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            //如果缓存不为null，直接返回
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return result;
        }

        System.out.println("查询了数据库！");

        /**
         * 1、将数据库多次查询变为一次
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //1、查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //2、封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类，查到这个一级类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1、找当前分类下的二级分类的三级分类封装成vo
                    List<CategoryEntity> lv3Catalog = getParent_cid(selectList, l2.getCatId());
                    if (lv3Catalog != null) {
                        List<Catelog2Vo.Catalog3Vo> collect = lv3Catalog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catelog2Vo.Catalog3Vo catalog3Vo = new Catelog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));

        //3、查到的数据放入缓存,将对象转为JSON放在缓存中
        String s = JSON.toJSONString(parent_cid);
        redisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);
        return parent_cid;
    }

    //数据库查询并封装分类数据  +本地锁
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithLocalLock() {

        //只要是同一把锁就能锁住需要这个锁的所有线程
        //synchronized(this)或者在方法上加锁,SpringBoot所有的主键都是单例的
        //TODO 本地锁synchronized JUC(lock),在分布式情况下，想要锁住所有，必须使用分布式锁


        synchronized (this) {
            //得到锁应该在缓存中再去确定一次，如果没有才需要继续查询
            return getDataFromDbToRedis();
        }

    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;
    }

    //343,45,1
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }


}