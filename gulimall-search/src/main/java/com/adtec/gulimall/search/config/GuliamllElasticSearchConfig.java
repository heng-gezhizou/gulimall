package com.adtec.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GuliamllElasticSearchConfig {

    @Bean
    public RestHighLevelClient elasticConfig(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(
                        "39.102.58.201",9200,"http")));
        return client;
    }

}
