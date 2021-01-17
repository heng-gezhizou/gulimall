package com.adtec.gulimall.product.dao;

import com.adtec.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:21
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> querySearchAttr(@Param("attrIds") List<Long> attrIds);
}
