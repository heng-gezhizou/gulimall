package com.adtec.gulimall.product.service.impl;

import com.adtec.common.constant.ProductConstant;
import com.adtec.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.adtec.gulimall.product.dao.AttrGroupDao;
import com.adtec.gulimall.product.dao.CategoryDao;
import com.adtec.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.adtec.gulimall.product.entity.AttrGroupEntity;
import com.adtec.gulimall.product.entity.CategoryEntity;
import com.adtec.gulimall.product.service.CategoryService;
import com.adtec.gulimall.product.vo.AttrEntityVO;
import com.adtec.gulimall.product.vo.AttrRespVo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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

import com.adtec.gulimall.product.dao.AttrDao;
import com.adtec.gulimall.product.entity.AttrEntity;
import com.adtec.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    private CategoryService categoryService;

    @Resource
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private AttrGroupDao attrGroupDao;

    @Resource
    private AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrEntityVO attrEntityVO) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrEntityVO,attrEntity);
        //1.保存基本关系
        this.save(attrEntity);
        if(attrEntityVO.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //2.保存分组关联关系
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attrEntityVO.getAttrGroupId());
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }

    @Override
    public PageUtils queryAttr(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type","base".equalsIgnoreCase(attrType)? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if(catelogId != 0){
            queryWrapper.eq("catelog_id",catelogId);
        }
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> attr = records.stream().map((attrEntity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            //查询分类名称
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if(categoryEntity != null){
                attrRespVo.setCatelogName(categoryEntity.getName());
            }

            if("base".equalsIgnoreCase(attrType)){
                //查询分组名称
                QueryWrapper<AttrAttrgroupRelationEntity> attrWrapper = new QueryWrapper<AttrAttrgroupRelationEntity>();
                System.out.println("---->"+attrEntity.getAttrId());
                    attrWrapper.eq("attr_id", attrEntity.getAttrId());
                    AttrAttrgroupRelationEntity entity = attrAttrgroupRelationDao.selectOne(attrWrapper);
                    if(entity!=null && entity.getAttrGroupId()!=null){
                        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(entity.getAttrGroupId());
                        attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                    }
            }
            return attrRespVo;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(attr);
        return pageUtils;
    }

    @Override
    public AttrRespVo queryAttrInfo(Long attrId) {
        AttrRespVo attrRespVo = new AttrRespVo();
        AttrEntity attrEntity = attrDao.selectById(attrId);
        BeanUtils.copyProperties(attrEntity,attrRespVo);

        //设置分组信息
        //查询pms_attr_attrgroup_relation表中的attr_group_id
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if(attrAttrgroupRelationEntity != null){
                attrRespVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if(attrGroupEntity != null){
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

        }

        //查询分类信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.getCatelogPathById(catelogId);
        attrRespVo.setCatelogPath(catelogPath);

        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        if (categoryEntity != null){
            attrRespVo.setCatelogName(categoryEntity.getName());
        }

        return attrRespVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrEntityVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //修改分组
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());

            Integer count = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if(count != 0){
                attrAttrgroupRelationDao.update(attrAttrgroupRelationEntity,new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
            }else {
                attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }

    }

    @Override
    public PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params) {
        System.out.println("????????????????????");
        //获取当前属性分组下的三级分类id
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();

        //通过三级分类id查询该三级分类下所有的属性分组
        List<AttrGroupEntity> catelogIdList = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> attrGroupIdList = catelogIdList.stream().map((item -> {
            return item.getAttrGroupId();
        })).collect(Collectors.toList());
        List<AttrAttrgroupRelationEntity> entities = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id",attrGroupIdList));

        List<Long> collect = entities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //通过三级分类查询该分类下有多少基本属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (collect != null && collect.size() > 0){
            wrapper.notIn("attr_id",collect);
        }
//        List<AttrEntity> list = attrDao.selectList(wrapper);
        String key = (String) params.get("key");
        if(!org.springframework.util.StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        PageUtils pageUtils = new PageUtils(page);

        return pageUtils;
    }

}