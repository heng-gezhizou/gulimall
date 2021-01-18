package com.adtec.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.adtec.common.to.HasStockTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.adtec.gulimall.ware.entity.WareSkuEntity;
import com.adtec.gulimall.ware.service.WareSkuService;
import com.adtec.common.utils.PageUtils;
import com.adtec.common.utils.R;



/**
 * 商品库存
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-15 22:09:06
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    @PostMapping("/hasstock")
    public R hasStock(@RequestBody List<Long> skuIds){
        List<HasStockTo> list = wareSkuService.hasStock(skuIds);
        return R.ok().setData(list);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = wareSkuService.queryPage(params);
        PageUtils page = wareSkuService.queryWarePage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
