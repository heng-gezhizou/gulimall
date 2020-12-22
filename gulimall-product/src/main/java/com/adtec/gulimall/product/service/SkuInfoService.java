package com.adtec.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.adtec.common.utils.PageUtils;
import com.adtec.gulimall.product.entity.SkuInfoEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:19
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);
}

