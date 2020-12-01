package com.adtec.gulimall.product;

import com.adtec.gulimall.product.entity.BrandEntity;
import com.adtec.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {

    @Autowired
    private BrandService brandService;
//
//    @Resource
//    private OSSClient ossClient;

    @Test
    public void testOss() throws FileNotFoundException {

//// 上传文件流。
//        InputStream inputStream = new FileInputStream("C:\\Users\\hgzz\\Pictures\\guli\\1.jpg");
//        ossClient.putObject("gulimall-hgzz", "3.jpg", inputStream);
//
//// 关闭OSSClient。
//        ossClient.shutdown();
//        System.out.println("上传成功");
    }

    @Test
    public void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");
        brandService.save(brandEntity);
    }

    @Test
    public void test01(){
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper();
        wrapper.eq("brand_id",1l);
        BrandEntity one = brandService.getOne(wrapper);
        System.out.println(one);
    }

}
