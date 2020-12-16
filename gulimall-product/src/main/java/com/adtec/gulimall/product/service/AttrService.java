package com.adtec.gulimall.product.service;

import com.adtec.gulimall.product.vo.AttrEntityVO;
import com.adtec.gulimall.product.vo.AttrRespVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.adtec.common.utils.PageUtils;
import com.adtec.gulimall.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:21
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrEntityVO attrEntityVO);

    PageUtils queryAttr(Map<String, Object> params, Long catelogId, String attrType);

    AttrRespVo queryAttrInfo(Long attrId);

    void updateAttr(AttrEntityVO attr);

    //获取未与当前分组关联的规格参数
    PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params);
}

