package com.adtec.gulimall.product.service.impl;

import com.adtec.gulimall.product.vo.Catalog2Vo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
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
    @Override
    public Map<String, List<Catalog2Vo>> getCatalog() {
        /**
         * 1、空结果缓存：解决缓存穿透
         * 2、设置不过期时间（随机值）：解决缓存雪崩
         * 3、加上：解决缓存击穿
         */
        //1、引入缓存逻辑
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if(StringUtils.isEmpty(catalogJSON)){
            //2、从数据库中查出数据并封装格式
            Map<String, List<Catalog2Vo>> catalogFromDb = getCatalogFromDb();
            //3、将从数据库中查出的数据转为String
            String toJSONString = JSON.toJSONString(catalogFromDb);
            //4、将toJSONString存入redis
            redisTemplate.opsForValue().set("catalogJSON",toJSONString);
            return catalogFromDb;
        }
        //5、将从redis中查出的String转换为想要的格式（1,5这两个过程为：序列化与反序列化）
        Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalog2Vo>>>() {});
        return result;

    }

    public Map<String, List<Catalog2Vo>> getCatalogFromDb() {
        List<CategoryEntity> level1Categorys = getLevel1Categorys();
        Map<String, List<Catalog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(a -> a.getCatId().toString(), b -> {
            //根据一级分类id获取该一级分类下的所有二级分类信息
            List<CategoryEntity> catalog2List = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", b.getCatId()));
            List<Catalog2Vo> catalog2VoList = catalog2List.stream().map(l2 -> {
                Catalog2Vo catalog2Vo = new Catalog2Vo(b.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                List<CategoryEntity> catalog3List = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));
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