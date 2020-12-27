package com.adtec.gulimall.product.service;

import com.adtec.gulimall.product.entity.ProductAttrValueEntity;
import com.adtec.gulimall.product.vo.AttrRelationVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.adtec.common.utils.PageUtils;
import com.adtec.gulimall.product.entity.AttrAttrgroupRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:21
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void deleteRelation(AttrRelationVo[] attrRelationVos);

    void addAttrRelation(List<AttrRelationVo> attrRelationVos);

}

