package com.adtec.gulimall.product.service;

import com.adtec.gulimall.product.entity.AttrEntity;
import com.adtec.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.adtec.common.utils.PageUtils;
import com.adtec.gulimall.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:20
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, long catelogId);

    List<AttrEntity> getAttrList(Long attrgroupId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrs(Long catelogId);
}

