package com.adtec.gulimall.ware.service.impl;

import com.adtec.common.constant.WareConstant;
import com.adtec.common.utils.R;
import com.adtec.gulimall.ware.entity.PurchaseDetailEntity;
import com.adtec.gulimall.ware.entity.WareSkuEntity;
import com.adtec.gulimall.ware.feign.ProductFeignService;
import com.adtec.gulimall.ware.service.PurchaseDetailService;
import com.adtec.gulimall.ware.service.WareSkuService;
import com.adtec.gulimall.ware.vo.MergeVo;
import com.adtec.gulimall.ware.vo.PurchaseDoneItemVo;
import com.adtec.gulimall.ware.vo.PurchaseDoneVo;
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

import com.adtec.gulimall.ware.dao.PurchaseDao;
import com.adtec.gulimall.ware.entity.PurchaseEntity;
import com.adtec.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Resource
    private PurchaseDetailService purchaseDetailService;

    @Resource
    private WareSkuService wareSkuService;

    @Resource
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryUnreceivePage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void merge(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        //没有选择采购单,新建一个采购单
        if (purchaseId == null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        Long finalPurchaseId = purchaseId;
        mergeVo.getItems().forEach(i->{
            PurchaseDetailEntity byId = purchaseDetailService.getById(i);
            if(byId.getStatus() == WareConstant.PurchaseDetailEnum.CREATED.getCode() || byId.getStatus() == WareConstant.PurchaseDetailEnum.ASSIGNED.getCode()){
                List<PurchaseDetailEntity> collect = mergeVo.getItems().stream().map(item -> {
                    PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                    purchaseDetailEntity.setId(item);
                    purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                    purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailEnum.ASSIGNED.getCode());

                    return purchaseDetailEntity;
                }).collect(Collectors.toList());
                purchaseDetailService.updateBatchById(collect);

                PurchaseEntity purchaseEntity = new PurchaseEntity();
                purchaseEntity.setUpdateTime(new Date());
                purchaseEntity.setId(finalPurchaseId);
                this.updateById(purchaseEntity);
            }
        });
    }

    @Override
    public void receivePurchase(List<Long> ids) {
        //确认当前采购单状态为新建或已领取
        List<PurchaseEntity> collect = ids.stream().map(item -> {
            PurchaseEntity entity = this.getById(item);
            return entity;
        }).filter(item -> {
            if (item.getStatus() == WareConstant.PurchaseEnum.CREATED.getCode() || item.getStatus() == WareConstant.PurchaseEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseEnum.RECEIVED.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        //采购单状态变为已领取
        this.updateBatchById(collect);

        //采购需求状态变为已领取
        collect.forEach(item->{
            List<PurchaseDetailEntity> list = purchaseDetailService.getPurchaseDetailById(item.getId());
            List<PurchaseDetailEntity> collect1 = list.stream().map(i -> {
                PurchaseDetailEntity entity = new PurchaseDetailEntity();
                BeanUtils.copyProperties(i, entity);
                entity.setStatus(WareConstant.PurchaseDetailEnum.BUYING.getCode());
                return entity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect1);
        });
    }

    @Transactional
    @Override
    public void done(PurchaseDoneVo vo) {
        //用于判断该采购单是否全部完成
        Boolean flag = true;
        List<PurchaseDoneItemVo> items = vo.getItems();
        for (PurchaseDoneItemVo item : items) {
            if(item.getStatus() == WareConstant.PurchaseDetailEnum.HASERROR.getCode()){
                flag = false;
                //3.修改采购项目状态(采购失败)
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(item.getItemId());
                purchaseDetailEntity.setStatus(item.getStatus());
                purchaseDetailService.updateById(purchaseDetailEntity);
            }else{
                //3.修改采购项目状态(采购成功)
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(item.getItemId());
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailEnum.FINISHED.getCode());
                purchaseDetailService.updateById(purchaseDetailEntity);

                //获取该商品详情
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());

                //判断该商品在库存中是否存在
                WareSkuEntity wareSkuEntity = wareSkuService.getOne(new QueryWrapper<WareSkuEntity>().eq("ware_id", detailEntity.getWareId()).eq("sku_id", detailEntity.getSkuId()));
                if (wareSkuEntity != null){
                    //修改库存 需要三个参数:wareId,skuId,skuNum
                    wareSkuService.updateStock(detailEntity.getWareId(),detailEntity.getSkuId(),detailEntity.getSkuNum());
                }else{
                    WareSkuEntity skuEntity = new WareSkuEntity();
                    skuEntity.setSkuId(detailEntity.getSkuId());
                    skuEntity.setStock(detailEntity.getSkuNum());
                    skuEntity.setWareId(detailEntity.getWareId());
                    skuEntity.setStockLocked(0);
                    try {
                        //TODO 远程调用product查询skuName
                        R info = productFeignService.info(detailEntity.getSkuId());
                        Map<String,Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                        if(info.getCode() == 0){
                            skuEntity.setSkuName((String) skuInfo.get("skuName"));
                        }
                    }catch (Exception e){}

                    //添加库存
                    wareSkuService.save(skuEntity);
                }


            }
        }

        //2.修改采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setStatus(flag?WareConstant.PurchaseEnum.FINISHED.getCode():WareConstant.PurchaseEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        purchaseEntity.setId(vo.getId());
        this.updateById(purchaseEntity);


    }

}