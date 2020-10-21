package com.adtec.gulimall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 *如何使用nacos作为配置中心？
 *
 * 1）引入依赖
 * <dependency>
 *      <groupId>com.alibaba.cloud</groupId>
 *      <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
 * </dependency>
 *
 * 2）在src\main\resources目录下新建一个bootstrap.properties文件
 * spring.application.name=gulimall-coupon
 * spring.cloud.nacos.config.server-addr=39.102.58.201:8848
 *
 * 3）需要在nacos的配置中心中默认添加一个叫（数据集）应用名.properties
 *4）给应用名.properties添加配置
 * 5）动态获取配置
 * @RefreshScope（动态获取并刷新配置）
 * @Value("${配置项的名}")（获取某个配置的值）
 *6）如果配置中心和当前应用配置了相同的配置项，优先使用配置中心的配置项
 *
 */


@EnableDiscoveryClient
@SpringBootApplication
public class GulimallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}
