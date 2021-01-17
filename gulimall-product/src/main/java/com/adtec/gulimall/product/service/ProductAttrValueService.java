package com.adtec.gulimall.product.service;

import com.adtec.gulimall.product.vo.savo.BaseAttrs;
import com.baomidou.mybatisplus.extension.service.IService;
import com.adtec.common.utils.PageUtils;
import com.adtec.gulimall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:20
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttrValue(Long id, List<BaseAttrs> baseAttrs);

    /**
     * 查询基础属性
     * @param spuId
     * @return
     */
    List<ProductAttrValueEntity> queryListSpu(Long spuId);

    void updateSpuInfo(Long spuId, List<ProductAttrValueEntity> attrList);

}

