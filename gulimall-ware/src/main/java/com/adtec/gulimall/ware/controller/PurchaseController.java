package com.adtec.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.adtec.gulimall.ware.vo.MergeVo;
import com.adtec.gulimall.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.adtec.gulimall.ware.entity.PurchaseEntity;
import com.adtec.gulimall.ware.service.PurchaseService;
import com.adtec.common.utils.PageUtils;
import com.adtec.common.utils.R;



/**
 * 采购信息
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-15 22:09:06
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    ///ware/purchase/done
    @PostMapping("/done")
    public R done(@RequestBody PurchaseDoneVo vo){
        purchaseService.done(vo);
        return R.ok();
    }

//    /ware/purchase/received
    @PostMapping("/received")
    public R receivePurchase(@RequestBody List<Long> ids){
        purchaseService.receivePurchase(ids);
        return R.ok();
    }

//    /ware/purchase/merge
    @PostMapping("/merge")
    public R mergePurchase(@RequestBody MergeVo mergeVo){
        purchaseService.merge(mergeVo);

        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/unreceive/list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryUnreceivePage(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
