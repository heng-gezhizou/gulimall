package com.adtec.gulimall.product.service.impl;

import com.adtec.common.to.SkuFullReductionTo;
import com.adtec.common.to.SpuBoundTo;
import com.adtec.common.utils.R;
import com.adtec.gulimall.product.entity.*;
import com.adtec.gulimall.product.feign.CouponFeignService;
import com.adtec.gulimall.product.service.*;
import com.adtec.gulimall.product.vo.savo.*;
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

    @Resource
    private SkuInfoService skuInfoService;

    @Resource
    private SkuImagesService skuImagesService;

    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Resource
    private CouponFeignService couponFeignService;

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
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        Bounds bounds = vo.getBounds();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        //使用feigh调用远程接口
        R r = couponFeignService.saveSpuBound(spuBoundTo);
        if(r.getCode() != 0){
            log.error("远程保存spu积分信息失败");
        }

        //6、保存当前商品spu对应的所有sku

        List<Skus> skus = vo.getSkus();
    //        private String skuName;
    //        private BigDecimal price;
    //        private String skuTitle;
    //        private String skuSubtitle;

            if(skus != null && skus.size() > 0){
                skus.forEach(sku->{
                    String defaultImg = "";
                    for (Images image : sku.getImages()) {
                        if(image.getDefaultImg() == 1){
                            defaultImg = image.getImgUrl();
                        }
                    }
                    SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                    BeanUtils.copyProperties(sku,skuInfoEntity);
                    skuInfoEntity.setSpuId(spuInfoEntity.getId());
                    skuInfoEntity.setSaleCount(0L);
                    skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                    skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                    skuInfoEntity.setSkuDefaultImg(defaultImg);

                    //6.1)、sku的基本信息（pms_sku_info）
                    skuInfoService.saveSkuInfo(skuInfoEntity);

                    Long skuId = skuInfoEntity.getSkuId();
                    List<SkuImagesEntity> imagesEntities = sku.getImages().stream().map(image -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        BeanUtils.copyProperties(image, skuImagesEntity);
                        skuImagesEntity.setSkuId(skuId);

                        return skuImagesEntity;
                    }).collect(Collectors.toList());

                    //6.2)、sku的图片信息（pms_sku_images）
                    skuImagesService.saveBatch(imagesEntities);

                    List<Attr> attr = sku.getAttr();
                    List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(item -> {
                        SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                        BeanUtils.copyProperties(item, skuSaleAttrValueEntity);
                        skuSaleAttrValueEntity.setSkuId(skuId);

                        return skuSaleAttrValueEntity;
                    }).collect(Collectors.toList());

                    //6.3)、sku的销售属性（pms_sku_sale_attr_value）
                    skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                    //6.4)、sku的优惠，满减属性（gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price）
                    SkuFullReductionTo skuFullReductionTo = new SkuFullReductionTo();
                    BeanUtils.copyProperties(sku,skuFullReductionTo);
                    skuFullReductionTo.setSkuId(skuId);
                    R r1 = couponFeignService.saveReduction(skuFullReductionTo);
                    if(r1.getCode() != 0){
                        log.error("远程保存sku优惠，满减信息失败");
                    }

                });
            }


    }

}