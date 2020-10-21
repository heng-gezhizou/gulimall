package com.adtec.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.adtec.common.utils.PageUtils;
import com.adtec.gulimall.ware.entity.WareSkuEntity;

import java.util.Map;

/**
 * 商品库存
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-15 22:09:06
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

