package com.adtec.gulimall.product.dao;

import com.adtec.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:20
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
