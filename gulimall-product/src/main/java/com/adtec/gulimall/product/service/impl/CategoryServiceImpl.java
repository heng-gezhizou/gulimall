package com.adtec.gulimall.product.service.impl;

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


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

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