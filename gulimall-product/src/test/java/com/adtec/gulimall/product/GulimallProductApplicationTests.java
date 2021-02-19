package com.adtec.gulimall.product;

import com.adtec.gulimall.product.entity.BrandEntity;
import com.adtec.gulimall.product.service.BrandService;
import com.adtec.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.RedissonLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void testRedisson(){
        System.out.println(redissonClient);
    }

    @Test
    public void testRedisTemplate(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("Hello","World_"+ UUID.randomUUID().toString());
        String hello = ops.get("Hello");
        System.out.println("Hello" + hello);
    }
//
//    @Resource
//    private OSSClient ossClient;
    @Test
    public void testCategoryService(){
        Long[] catelogPath = categoryService.getCatelogPathById(225L);
        log.info("完整路径：{}", Arrays.asList(catelogPath));
    }

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
