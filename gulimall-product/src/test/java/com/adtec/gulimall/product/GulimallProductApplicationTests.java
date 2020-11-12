package com.adtec.gulimall.product;

import com.adtec.gulimall.product.entity.BrandEntity;
import com.adtec.gulimall.product.service.BrandService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
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

    @Resource
    private OSSClient ossClient;

    @Test
    public void testOss() throws FileNotFoundException {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "oss-cn-beijing.aliyuncs.com";
// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = "LTAI4G2K6gKUYNfJ3nUyFGGo";
        String accessKeySecret = "FIE2OS4ZMZg7OFkYXmi7med6tIem49";

// 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 上传文件流。
        InputStream inputStream = new FileInputStream("C:\\Users\\hgzz\\Pictures\\guli\\1.jpg");
        ossClient.putObject("gulimall-hgzz", "1.jpg", inputStream);

// 关闭OSSClient。
        ossClient.shutdown();
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
