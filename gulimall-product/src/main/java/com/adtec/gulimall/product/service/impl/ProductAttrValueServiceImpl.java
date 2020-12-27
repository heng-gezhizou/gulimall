package com.adtec.gulimall.product.service.impl;

import com.adtec.gulimall.product.entity.AttrEntity;
import com.adtec.gulimall.product.service.AttrService;
import com.adtec.gulimall.product.vo.savo.BaseAttrs;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adtec.common.utils.PageUtils;
import com.adtec.common.utils.Query;

import com.adtec.gulimall.product.dao.ProductAttrValueDao;
import com.adtec.gulimall.product.entity.ProductAttrValueEntity;
import com.adtec.gulimall.product.service.ProductAttrValueService;

import javax.annotation.Resource;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Resource
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveAttrValue(Long id, List<BaseAttrs> baseAttrs) {
        if(baseAttrs == null || baseAttrs.size() == 0){

        }else{
            List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                productAttrValueEntity.setSpuId(id);
                productAttrValueEntity.setAttrId(attr.getAttrId());
                //页面没有传规格名称，需要查表
                AttrEntity entity = attrService.getById(attr.getAttrId());
                productAttrValueEntity.setAttrName(entity.getAttrName());
                productAttrValueEntity.setAttrValue(attr.getAttrValues());
                productAttrValueEntity.setQuickShow(attr.getShowDesc());

                return productAttrValueEntity;
            }).collect(Collectors.toList());

            this.saveBatch(collect);
        }
    }

    @Override
    public List<ProductAttrValueEntity> queryListSpu(Long spuId) {
        List<ProductAttrValueEntity> entities = this.baseMapper.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return entities;
    }

    @Override
    public void updateSpuInfo(Long spuId, List<ProductAttrValueEntity> attrList) {
        this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));

        List<ProductAttrValueEntity> collect = attrList.stream().map(item -> {
            item.setSpuId(spuId);
            return item;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

}