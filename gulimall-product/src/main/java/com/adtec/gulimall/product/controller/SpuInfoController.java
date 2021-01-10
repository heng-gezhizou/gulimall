package com.adtec.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.adtec.gulimall.product.vo.savo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.adtec.gulimall.product.entity.SpuInfoEntity;
import com.adtec.gulimall.product.service.SpuInfoService;
import com.adtec.common.utils.PageUtils;
import com.adtec.common.utils.R;



/**
 * spu信息
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:19
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 商品上架
     */
    @PostMapping("/{spuId}/up")
    public R productUp(@PathVariable("spuId") Long spuId){
        spuInfoService.up(spuId);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuSaveVo vo){
//		spuInfoService.save();
        spuInfoService.saveSpuInfo(vo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
