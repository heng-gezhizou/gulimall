package com.adtec.gulimall.ware.service;

import com.adtec.common.to.HasStockTo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.adtec.common.utils.PageUtils;
import com.adtec.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
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

    PageUtils queryWarePage(Map<String, Object> params);

    void updateStock(Long wareId, Long skuId, Integer skuNum);

    /**
     * 查询sku是否有库存
     * @param skuIds
     * @return
     */
    List<HasStockTo> hasStock(List<Long> skuIds);
}

