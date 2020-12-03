package com.adtec.gulimall.product.service;

import com.adtec.gulimall.product.vo.AttrEntityVO;
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
}

