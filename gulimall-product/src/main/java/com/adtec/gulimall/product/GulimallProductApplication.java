package com.adtec.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
/**
 * 5、模板引擎
 * 1) 、 pom引入thymeleaf-starter:关闭缓存
 * 2)、静态资源都放在static文件夹下就可以按照路径直接访问
 * 3)、页面放在templates下，直接访问
 * SpringBoot，访间项目的时候,默认会找index
 * 4)、页面修改不重启服务器实时更新
 *      1) 、引入dev-tools
 *      2)、修改完页面controller shift f9重新自动编译下页面
 * 6、redis
 * 1)、pom文件引入starter-data-redis
 * 2)、application.yml文件中添加redis的相关配置
 * 3)、使用SpringBoot自动配置好的StringRedisTemplate来操作redis，存放数据key，数据值value
 *
 *
 */
@EnableFeignClients(basePackages = "com.adtec.gulimall.product.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
