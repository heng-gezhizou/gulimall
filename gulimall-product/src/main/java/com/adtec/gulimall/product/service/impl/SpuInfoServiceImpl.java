package com.adtec.gulimall.product.service.impl;

import com.adtec.gulimall.product.entity.SpuInfoDescEntity;
import com.adtec.gulimall.product.service.ProductAttrValueService;
import com.adtec.gulimall.product.service.SpuImagesService;
import com.adtec.gulimall.product.service.SpuInfoDescService;
import com.adtec.gulimall.product.vo.savo.BaseAttrs;
import com.adtec.gulimall.product.vo.savo.Skus;
import com.adtec.gulimall.product.vo.savo.SpuSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adtec.common.utils.PageUtils;
import com.adtec.common.utils.Query;

import com.adtec.gulimall.product.dao.SpuInfoDao;
import com.adtec.gulimall.product.entity.SpuInfoEntity;
import com.adtec.gulimall.product.service.SpuInfoService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Resource
    private SpuInfoDescService spuInfoDescService;

    @Resource
    private SpuImagesService spuImagesService;

    @Resource
    private ProductAttrValueService productAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {

        //1、保存商品的spu基本信息(pms_spu_info)
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.save(spuInfoEntity);

        //2、保存商品描述图片(pms_spu_info_desc)
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuDesript(spuInfoDescEntity);

        //3、保存商品图片集(pms_spu_images)
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),images);

        //4、保存商品规格参数(pms_product_attr_value)
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        productAttrValueService.saveAttrValue(spuInfoEntity.getId(),baseAttrs);

        //5、保存商品积分信息（gulimall_sms->sms_spu_bounds）
        //6、保存当前商品spu对应的所有sku
          //6.1)、sku的基本信息（pms_sku_info）
        List<Skus> skus = vo.getSkus();
        //6.2)、sku的图片信息（pms_sku_images）
          //6.3)、sku的销售属性（pms_sku_sale_attr_value）
          //6.4)、sku的优惠，满减属性（gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price）
    }

}