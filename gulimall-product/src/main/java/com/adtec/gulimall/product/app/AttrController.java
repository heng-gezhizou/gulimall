package com.adtec.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.adtec.gulimall.product.entity.ProductAttrValueEntity;
import com.adtec.gulimall.product.service.AttrAttrgroupRelationService;
import com.adtec.gulimall.product.service.ProductAttrValueService;
import com.adtec.gulimall.product.vo.AttrEntityVO;
import com.adtec.gulimall.product.vo.AttrRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.adtec.gulimall.product.entity.AttrEntity;
import com.adtec.gulimall.product.service.AttrService;
import com.adtec.common.utils.PageUtils;
import com.adtec.common.utils.R;



/**
 * 商品属性
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:21
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    ///product/attr/update/{spuId}
    @PostMapping("/update/{spuId}")
    public R updateSpuInfo(@PathVariable("spuId") Long spuId,@RequestBody List<ProductAttrValueEntity> attrList){
        productAttrValueService.updateSpuInfo(spuId,attrList);
        return R.ok();
    }

//    /product/attr/base/listforspu/{spuId}
    @GetMapping("/base/listforspu/{spuId}")
    public R queryListForSpu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> list = productAttrValueService.queryListSpu(spuId);
        return R.ok().put("data",list);
    }

    @GetMapping("/{attrType}/list/{catelogId}")
    public R queryAttr(@RequestParam Map<String,Object> params,@PathVariable("catelogId") Long catelogId,
                       @PathVariable("attrType") String attrType){
        PageUtils pageUtils = attrService.queryAttr(params,catelogId,attrType);
        return R.ok().put("page",pageUtils);
    }

    @GetMapping("/info/{attrId}")
    public R queryAttrInfo(@PathVariable("attrId") Long attrId){
        AttrRespVo attr = attrService.queryAttrInfo(attrId);
        return R.ok().put("attr",attr);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
		AttrEntity attr = attrService.getById(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrEntityVO attrEntityVO){
		attrService.saveAttr(attrEntityVO);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrEntityVO attr){
//		attrService.updateById(attr);
        attrService.updateAttr(attr);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
