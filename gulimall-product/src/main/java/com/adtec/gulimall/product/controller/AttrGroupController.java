package com.adtec.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.adtec.gulimall.product.entity.AttrEntity;
import com.adtec.gulimall.product.service.AttrAttrgroupRelationService;
import com.adtec.gulimall.product.service.CategoryService;
import com.adtec.gulimall.product.vo.AttrRelationVo;
import com.adtec.gulimall.product.vo.AttrRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.adtec.gulimall.product.entity.AttrGroupEntity;
import com.adtec.gulimall.product.service.AttrGroupService;
import com.adtec.common.utils.PageUtils;
import com.adtec.common.utils.R;



/**
 * 属性分组
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:20
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * created by hgzz
     */
    //      /product/attrgroup/attr/relation/delete
    @PostMapping("/attr/relation/delete")
    public R deleteAttrRelation(@RequestBody AttrRelationVo[] attrRelationVos){
        attrAttrgroupRelationService.deleteRelation(attrRelationVos);
        return R.ok();
    }

    /**
     * created by hgzz
     */
    //    /product/attrgroup/{attrgroupId}/attr/relation
    @GetMapping("/{attrgroupId}/attr/relation")
    public R getRelation(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> list = attrGroupService.getAttrList(attrgroupId);
        return R.ok().put("data",list);
    }

    /**
     * created by hgzz
     */
//    /product/attrgroup/{attrgroupId}/noattr/relation
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R getNoRelation(@PathVariable("attrgroupId") Long attrgroupId,
                           @RequestBody Map<String,Object> params){
        PageUtils page = attrGroupService.getNoRelationAttr(attrgroupId,params);
        return R.ok().put("page",page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable long catelogId){
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     * param: attrGroupId(为attrGroup表的id)
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
		Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.getCatelogPathById(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
