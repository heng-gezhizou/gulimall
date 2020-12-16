package com.adtec.gulimall.product.service.impl;

import com.adtec.common.constant.ProductConstant;
import com.adtec.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.adtec.gulimall.product.dao.AttrDao;
import com.adtec.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.adtec.gulimall.product.entity.AttrEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adtec.common.utils.PageUtils;
import com.adtec.common.utils.Query;

import com.adtec.gulimall.product.dao.AttrGroupDao;
import com.adtec.gulimall.product.entity.AttrGroupEntity;
import com.adtec.gulimall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    private AttrAttrgroupRelationDao relationDao;

    @Resource
    private AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, long catelogId) {
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((obj) -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        if(catelogId == 0){
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }else {
            wrapper.eq("catelog_id", catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
    }

    @Override
    public List<AttrEntity> getAttrList(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id",attrgroupId));
        List<Long> collect = entities.stream().map((item) -> {
            return item.getAttrId();
        }).collect(Collectors.toList());

        List<AttrEntity> attrEntities = attrDao.selectBatchIds(collect);
        return attrEntities;
    }

    @Override
    public PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params) {
        //获取当前属性分组下的三级分类id
        AttrGroupEntity attrGroupEntity = this.baseMapper.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();

        //通过三级分类id查询该三级分类下所有的属性分组
        List<AttrGroupEntity> catelogIdList = this.baseMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> attrGroupIdList = catelogIdList.stream().map((item -> {
            return item.getAttrGroupId();
        })).collect(Collectors.toList());
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id",attrGroupIdList));

        List<Long> collect = entities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //通过三级分类查询该分类下有多少基本属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (collect != null && collect.size() > 0){
            wrapper.notIn("attr_id",collect);
        }
        List<AttrEntity> list = attrDao.selectList(wrapper);
        String key = (String) params.get("key");




        return null;
    }
}