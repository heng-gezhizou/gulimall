package com.adtec.gulimall.product;

import com.adtec.gulimall.product.entity.BrandEntity;
import com.adtec.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private BrandService brandService;

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");
        brandService.save(brandEntity);
    }

    @Test
    void test01(){
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper();
        wrapper.eq("brand_id",1l);
        BrandEntity one = brandService.getOne(wrapper);
        System.out.println(one);
    }

}
