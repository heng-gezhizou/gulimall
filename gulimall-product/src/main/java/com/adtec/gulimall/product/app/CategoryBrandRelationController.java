package com.adtec.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.adtec.gulimall.product.entity.BrandEntity;
import com.adtec.gulimall.product.vo.CategoryBrandVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.adtec.gulimall.product.entity.CategoryBrandRelationEntity;
import com.adtec.gulimall.product.service.CategoryBrandRelationService;
import com.adtec.common.utils.PageUtils;
import com.adtec.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:20
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * /product/categorybrandrelation/brands/list
     * hgzz
     *
     */
    @GetMapping("/brands/list")
    public R getBrandList(@RequestParam Long catId){
        List<BrandEntity> brandList = categoryBrandRelationService.getBrandList(catId);
        List<Object> collect = brandList.stream().map(item -> {
            CategoryBrandVo categoryBrandVo = new CategoryBrandVo();
            categoryBrandVo.setBrandId(item.getBrandId());
            categoryBrandVo.setBrandName(item.getName());
            return categoryBrandVo;
        }).collect(Collectors.toList());
        return R.ok().put("data",collect);
    }

    @GetMapping("/catelog/list")
    public R relationList(@RequestParam Long brandId){
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
        return R.ok().put("data", data);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
