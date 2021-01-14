package com.adtec.gulimall.ware.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adtec.common.utils.PageUtils;
import com.adtec.common.utils.Query;

import com.adtec.gulimall.ware.dao.WareSkuDao;
import com.adtec.gulimall.ware.entity.WareSkuEntity;
import com.adtec.gulimall.ware.service.WareSkuService;

import javax.annotation.Resource;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    private WareSkuDao wareSkuDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }

        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryWarePage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }

        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper

        );

        return new PageUtils(page);
    }

    @Override
    public void updateStock(Long wareId, Long skuId, Integer skuNum) {
        wareSkuDao.updateStock(wareId,skuId,skuNum);
    }

}