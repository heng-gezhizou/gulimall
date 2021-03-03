package com.adtec.gulimall.product.service.impl;

import com.adtec.gulimall.product.vo.Catalog2Vo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adtec.common.utils.PageUtils;
import com.adtec.common.utils.Query;

import com.adtec.gulimall.product.dao.CategoryDao;
import com.adtec.gulimall.product.entity.CategoryEntity;
import com.adtec.gulimall.product.service.CategoryService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listTree() {

        //查出所有分类
        List<CategoryEntity> list = baseMapper.selectList(null);
        System.out.println(list);
        //组装成父子的树形结构
        //查询出所有的一级分类
        List<CategoryEntity> level1Menu = list.stream().filter((categoryEntity) -> {
            //这里需要为true才能不被过滤
            return categoryEntity.getParentCid() == 0;
        }).map((menu)->{
            //通过递归获取子数据
            menu.setChildren(getMenuChildren(menu,list));
            return menu;
        }).sorted((menu1,menu2)->{
            //排序
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());

        return level1Menu;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查要删除的菜单是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] getCatelogPathById(Long attrGroupId) {
        List<Long> list = new ArrayList<>();
        List<Long> listPath = new ArrayList<>();
        listPath = getParentPath(attrGroupId,list);
        Collections.reverse(listPath);
        return listPath.toArray(new Long[listPath.size()]);
    }

    @Cacheable(value = {"category"},key = "#root.method.name")
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("执行了。。。");
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    /**
     * TODO 产生堆外内存溢出: OutOfDirectMemoryError
     * 1)、springboot2.0后默认使用lettuce作为操作redis的客户端。它使用netty进行网络通信。
     * 2)、lettuce的bug导致netty堆外内存溢出，netty如果没有指定堆外内存，默认使用-Xmx100m
     * 可以通过-Dio.netty.maxDirectMemory去调大堆外内存。
     * 解决方案:
     * 1)、升级lettuce客户端。   2)、切换使用jedis
     * 补充：lettuce，jedis是操作redis的底层，Spring在其基础上封装了RedisTemplate。
     *
     */
    @Cacheable(value = {"category"},key = "'catalogJSON'")
    @Override
    public Map<String, List<Catalog2Vo>> getCatalog() {
        System.out.println("测试");
        /**
         * 1、空结果缓存：解决缓存穿透
         * 2、设置不过期时间（随机值）：解决缓存雪崩
         * 3、加上：解决缓存击穿
         */
        //1、引入缓存逻辑
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if(StringUtils.isEmpty(catalogJSON)){
            //缓存中没有数据，需要查询数据库
            //2、从数据库中查出数据并封装格式
            System.out.println("缓存中无数据，需要查询数据库");
            Map<String, List<Catalog2Vo>> catalogFromDb = getCatalogFromDb();
            //3、将从数据库中查出的数据转为String
//            String toJSONString = JSON.toJSONString(catalogFromDb);
            //4、将toJSONString存入redis
//            redisTemplate.opsForValue().set("catalogJSON",toJSONString, 1,TimeUnit.DAYS);
            return catalogFromDb;
        }
        System.out.println("缓存中有数据。。。。");
        //5、将从redis中查出的String转换为想要的格式（1,5这两个过程为：序列化与反序列化）
        Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalog2Vo>>>() {});
        return result;

    }

    @CacheEvict(value = "category",allEntries = true)
    @Override
    public void updateCategory(CategoryEntity category) {
        System.out.println("更新啦");
        this.updateById(category);
    }

    public Map<String, List<Catalog2Vo>> getCatalogFromDb() {
//        List<CategoryEntity> level1Categorys = getLevel1Categorys();
        System.out.println("准备查询数据库......");
        /**
         * 本地锁：synchronized，JUC(Lock)
         * 问题：单服务器没有问题，分布式情况下存在多个请求查询数据库的情况
         */

        /**
         * 业务优化：减少查库次数，一次查询所有需要的数据，存入list中
         */
        //本地代码块锁，springboot的所有组件都是单例模式的，同一个服务中只有一个对象
//        synchronized (this){
//            return getCategoryFromDbWithRedisson();
//        }
        /**
         * 引入分布式锁
         */
        RLock lock = redissonClient.getLock("catelog-fromdb");
        lock.lock();
        Map<String, List<Catalog2Vo>> result = null;
        try{
            result =  getCategoryFromDbWithRedisson();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return result;
    }

    private Map<String, List<Catalog2Vo>> getCategoryFromDbWithRedisson() {
        System.out.println("查询了数据库......");
        List<CategoryEntity> selectList = baseMapper.selectList(null);

//        List<CategoryEntity> level1Categorys = getLevel1Categorys();
        List<CategoryEntity> level1Categorys = getParent_cid(selectList,0L);
        Map<String, List<Catalog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(a -> a.getCatId().toString(), b -> {
            //根据一级分类id获取该一级分类下的所有二级分类信息
            List<CategoryEntity> catalog2List = getParent_cid(selectList,b.getCatId());
            List<Catalog2Vo> catalog2VoList = catalog2List.stream().map(l2 -> {
                Catalog2Vo catalog2Vo = new Catalog2Vo(b.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                List<CategoryEntity> catalog3List = getParent_cid(selectList,l2.getCatId());
                List<Catalog2Vo.Catalog3Vo> catalog3VoList = catalog3List.stream().map(l3 -> {
                    Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName().toString());
                    return catalog3Vo;
                }).collect(Collectors.toList());
                catalog2Vo.setCatalog3List(catalog3VoList);
                return catalog2Vo;
            }).collect(Collectors.toList());
            return catalog2VoList;
        }));
        return collect;
    }

    private Map<String, List<Catalog2Vo>> getCategoryFromDbWithRedisson1() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if(!StringUtils.isEmpty(catalogJSON)){
            Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalog2Vo>>>() {});
            return result;
        }

        System.out.println("查询了数据库......");
        List<CategoryEntity> selectList = baseMapper.selectList(null);

//        List<CategoryEntity> level1Categorys = getLevel1Categorys();
        List<CategoryEntity> level1Categorys = getParent_cid(selectList,0L);
        Map<String, List<Catalog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(a -> a.getCatId().toString(), b -> {
            //根据一级分类id获取该一级分类下的所有二级分类信息
            List<CategoryEntity> catalog2List = getParent_cid(selectList,b.getCatId());
            List<Catalog2Vo> catalog2VoList = catalog2List.stream().map(l2 -> {
                Catalog2Vo catalog2Vo = new Catalog2Vo(b.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                List<CategoryEntity> catalog3List = getParent_cid(selectList,l2.getCatId());
                List<Catalog2Vo.Catalog3Vo> catalog3VoList = catalog3List.stream().map(l3 -> {
                    Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName().toString());
                    return catalog3Vo;
                }).collect(Collectors.toList());
                catalog2Vo.setCatalog3List(catalog3VoList);
                return catalog2Vo;
            }).collect(Collectors.toList());
            return catalog2VoList;
        }));

        String toJSONString = JSON.toJSONString(collect);
        //4、将toJSONString存入redis
        redisTemplate.opsForValue().set("catalogJSON",toJSONString, 1, TimeUnit.DAYS);
        return collect;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,Long parent_cid) {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", parent_cid));
    }

    public List<Long> getParentPath(Long catelogId,List<Long> list){
        list.add(catelogId);
        CategoryEntity categoryEntity = baseMapper.selectById(catelogId);
        if(categoryEntity.getParentCid() != 0){
            getParentPath(categoryEntity.getParentCid(),list);
        }
        return list;
    }



    private List<CategoryEntity> getMenuChildren(CategoryEntity entity,List<CategoryEntity> root){
        List<CategoryEntity> menuChild = root.stream().filter((categoryEntity -> {
            return categoryEntity.getParentCid() == entity.getCatId();
        })).map((menu)->{
            menu.setChildren(getMenuChildren(menu,root));
            return menu;
        }).sorted((menu1,menu2)->{
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());

        return menuChild;
    }

}