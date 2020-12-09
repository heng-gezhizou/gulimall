package com.adtec.gulimall.product.service.impl;

import com.adtec.gulimall.product.vo.AttrRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adtec.common.utils.PageUtils;
import com.adtec.common.utils.Query;

import com.adtec.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.adtec.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.adtec.gulimall.product.service.AttrAttrgroupRelationService;

import javax.annotation.Resource;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Resource
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void deleteRelation(AttrRelationVo[] attrRelationVos) {
        List<AttrAttrgroupRelationEntity>  attrRelation = Arrays.asList(attrRelationVos).stream().map((attrRelationVo) -> {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(attrRelationVo, entity);
            return entity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationDao.deleteRelationBatch(attrRelation);
    }

}