package com.adtec.gulimall.product.service.impl;

import com.adtec.common.constant.ProductConstant;
import com.adtec.common.to.HasStockTo;
import com.adtec.common.to.SkuFullReductionTo;
import com.adtec.common.to.SpuBoundTo;
import com.adtec.common.to.es.SkuEsModel;
import com.adtec.common.utils.R;
import com.adtec.gulimall.product.entity.*;
import com.adtec.gulimall.product.feign.CouponFeignService;
import com.adtec.gulimall.product.feign.SearchFeignService;
import com.adtec.gulimall.product.feign.WareFeignService;
import com.adtec.gulimall.product.service.*;
import com.adtec.gulimall.product.vo.savo.*;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
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

    @Resource
    private BrandService brandService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private AttrService attrService;

    @Resource
    private WareFeignService wareFeignService;

    @Resource
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)) {
            wrapper.and(item->{
                item.eq("id",key).or().like("spu_name",key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
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
                    }).filter(item->{
                        //stream流的filter如果返回true则不过滤,如果返回false则过滤
                        return !StringUtils.isEmpty(item.getImgUrl());
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
                    if(skuFullReductionTo.getFullCount() > 0 || skuFullReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                        R r1 = couponFeignService.saveReduction(skuFullReductionTo);
                        if(r1.getCode() != 0){
                            log.error("远程保存sku优惠，满减信息失败");
                        }
                    }
                });
            }


    }

    /**
     * 商城上架
     * @param spuId
     */
    @Override
    public void up(Long spuId) {

        List<SkuInfoEntity> skuInfoList = skuInfoService.getSkuInfoBySpuId(spuId);



        //TODO 5、查询当前sku所有可以被检索的规格属性
//            private List<Attr> attrs;
        List<ProductAttrValueEntity> attrEntities = productAttrValueService.queryListSpu(spuId);
        List<Long> attrIds = attrEntities.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        List<Long> searchAttrIds = attrService.querySearchAttr(attrIds);

        HashSet<Long> idSet = new HashSet<>(searchAttrIds);

        List<SkuEsModel.Attr> attrList = attrEntities.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attr attr = new SkuEsModel.Attr();
            BeanUtils.copyProperties(item, attr);
            return attr;
        }).collect(Collectors.toList());

        Map<Long, Boolean> stockMap = null;
        try{
            //获取sku集合
            List<Long> skuList = skuInfoList.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
            // TODO 1、发送远程调用查询仓库库存
            R r = wareFeignService.hasStock(skuList);
            TypeReference<List<HasStockTo>> typeReference = new TypeReference<List<HasStockTo>>() {
            };
            stockMap = r.getData(typeReference).stream().collect(Collectors.toMap(HasStockTo::getSkuId, HasStockTo::getHasStock));
        }catch (Exception e){
            log.error("库存服务查询异常，原因{}",e);
        }

        Map<Long, Boolean> finalStockMap = stockMap;
        //封装每个sku的信息
        List<SkuEsModel> collect = skuInfoList.stream().map(sku -> {
            SkuEsModel skuEsModel = new SkuEsModel();

            BeanUtils.copyProperties(sku, skuEsModel);

            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());

//            设置库存信息
            if (finalStockMap == null) {
                skuEsModel.setHasStock(true);
            } else {
                skuEsModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }

            // TODO 2、热点评分，写死为0
//            评分
//            private Long hotScore;
            skuEsModel.setHotScore(0L);

            // TODO 3、查询品牌信息
            BrandEntity brandEntity = brandService.getById(sku.getBrandId());
            skuEsModel.setBrandName(brandEntity.getName());
            skuEsModel.setBrandImg(brandEntity.getLogo());

            //TODO 4、查询分类名称
            CategoryEntity categoryEntity = categoryService.getById(sku.getCatalogId());
            skuEsModel.setCatalogName(categoryEntity.getName());
//            private String catalogName;

            skuEsModel.setAttrs(attrList);

            return skuEsModel;
        }).collect(Collectors.toList());

        //保存数据到es中
        R r = searchFeignService.productUp(collect);
        if (r.getCode() == 0){
            //调用远程服务保存数据到es成功，将spu状态修改为上架
            baseMapper.updateStatus(spuId, ProductConstant.StatusEnum.UP_SPU.getCode());
        }else{
            //远程调用失败
            //TODO 7、重复调用问题？接口幂等性；重试机制？
        }

    }

}