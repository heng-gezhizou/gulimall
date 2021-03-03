package com.adtec.gulimall.product.service;

import com.adtec.gulimall.product.vo.Catalog2Vo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.adtec.common.utils.PageUtils;
import com.adtec.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:20
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //查出分类的树状结构
    List<CategoryEntity> listTree();

    //删除菜单
    void removeMenuByIds(List<Long> asList);

    Long[] getCatelogPathById(Long attrGroupId);

    List<CategoryEntity> getLevel1Categorys();

    //获取分类
    Map<String, List<Catalog2Vo>> getCatalog();

    void updateCategory(CategoryEntity category);
}

