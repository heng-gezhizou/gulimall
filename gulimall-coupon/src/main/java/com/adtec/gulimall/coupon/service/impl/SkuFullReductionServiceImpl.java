package com.adtec.gulimall.coupon.service.impl;

import com.adtec.common.to.SkuFullReductionTo;
import com.adtec.gulimall.coupon.entity.MemberPriceEntity;
import com.adtec.gulimall.coupon.entity.SkuLadderEntity;
import com.adtec.gulimall.coupon.service.MemberPriceService;
import com.adtec.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adtec.common.utils.PageUtils;
import com.adtec.common.utils.Query;

import com.adtec.gulimall.coupon.dao.SkuFullReductionDao;
import com.adtec.gulimall.coupon.entity.SkuFullReductionEntity;
import com.adtec.gulimall.coupon.service.SkuFullReductionService;

import javax.annotation.Resource;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Resource
    private SkuLadderService skuLadderService;

    @Resource
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveReduction(SkuFullReductionTo skuFullReductionTo) {
        //sku的优惠，满减属性（gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price）
        //sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setAddOther(skuFullReductionTo.getCountStatus());
        BeanUtils.copyProperties(skuFullReductionTo,skuLadderEntity);
        skuLadderService.save(skuLadderEntity);

        //sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuFullReductionTo,skuFullReductionEntity);
//        skuFullReductionEntity.setAddOther(skuFullReductionTo.getCountStatus());
        this.save(skuFullReductionEntity);

//        sms_member_price
        List<MemberPriceEntity> collect = skuFullReductionTo.getMemberPrice().stream().map(item -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setMemberLevelId(item.getId());
            memberPriceEntity.setMemberLevelName(item.getName());
            memberPriceEntity.setMemberPrice(item.getPrice());
            memberPriceEntity.setSkuId(skuFullReductionTo.getSkuId());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);
    }

}