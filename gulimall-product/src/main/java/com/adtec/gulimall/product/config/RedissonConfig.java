package com.adtec.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://39.102.58.201:6379");
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
