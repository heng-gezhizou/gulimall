package com.adtec.gulimall.product.service;

import com.adtec.gulimall.product.entity.BrandEntity;
import com.adtec.gulimall.product.entity.CategoryEntity;
import com.adtec.gulimall.product.vo.CategoryBrandVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.adtec.common.utils.PageUtils;
import com.adtec.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:20
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrand(BrandEntity brand);

    void updateCategory(CategoryEntity category);

    List<BrandEntity> getBrandList(Long catId);
}

