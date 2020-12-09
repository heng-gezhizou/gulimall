package com.adtec.gulimall.product.dao;

import com.adtec.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.adtec.gulimall.product.vo.AttrRelationVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:21
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteRelationBatch(@Param("attrRelation") List<AttrAttrgroupRelationEntity> attrRelation);
}
