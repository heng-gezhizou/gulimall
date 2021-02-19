package com.adtec.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
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
 * 6、整合SpringCache
 * 1）引入依赖：spring-boot-starter-cache与spring-boot-starter-data-redis
 * 2）编写配置
 *
 * （1）缓存的自动配置了哪些？
 * CacheAutoConfiguration，会导入RedisCacheConfiguration
 * 自动配置了缓存管理器RedisCacheManager
 * （2）配置使用Redis作为缓存
 * 修改“application.properties”文件，指定使用redis作为缓存，spring.cache.type=redis
 * （3）和缓存有关的注解
 * @Cacheable: Triggers cache population. 触发将数据保存到缓存的操作
 * @CacheEvict: Triggers cache eviction.     触发将数据从缓存中删除的操作
 * @CachePut: Updates the cache without interfering with the method execution. 在不影响方法执行的情况下更新缓存。
 * @Caching: Regroups multiple cache operations to be applied on a method. 组合以上多个操作
 * @CacheConfig: Shares some common cache-related settings at class-level.在类级别上共享一些公共的与缓存相关的设置。
 * 3）测试使用缓存
 * （1）开启缓存功能，在主启动类上，标注@EnableCaching
 * （2）只需要使用注解，就可以完成缓存操作
 * （3）在业务方法的头部标上@Cacheable，加上该注解后，表示当前方法需要将进行缓存，如果缓存中有，方法无效调用，如果缓存中没有，则会调用方法，最后将方法的结果放入到缓存中。
 * （4）指定缓存分区。每一个需要缓存的数据，我们都需要来指定要放到哪个名字的缓存中。通常按照业务类型进行划分。
 *
 */
@EnableCaching
@EnableFeignClients(basePackages = "com.adtec.gulimall.product.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
