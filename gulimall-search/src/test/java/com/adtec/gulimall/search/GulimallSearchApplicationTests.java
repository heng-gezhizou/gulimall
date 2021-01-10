package com.adtec.gulimall.search;

import com.adtec.gulimall.search.config.GuliamllElasticSearchConfig;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.naming.directory.SearchResult;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallSearchApplicationTests {

    @Resource
    private RestHighLevelClient client;

    @Test
    public void contextLoads() {
        System.out.println(client);
    }

    @Test
    public void createIndexTest() throws IOException {
        IndexRequest request = new IndexRequest("user");
        User user = new User();
        user.name = "张三";
        user.gender = "女";
        String jsonString = JSONObject.toJSONString(user);
        request.id("1");
        request.source(jsonString, XContentType.JSON);

        //执行操作
        IndexResponse index = client.index(request, GuliamllElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(index);
    }
    class User{
        public String name;
        public String gender;
    }

    @Data
    @ToString
    static class Account {

        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }

    @Test
    public void getIndexTest() throws IOException {
        //1、创建查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");
        //2、添加查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //2.1、查询条件
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));

        //2.2、按照年龄的值分布进行聚合
        TermsAggregationBuilder aggAge = AggregationBuilders.terms("aggAge").field("age").size(10);
        searchSourceBuilder.aggregation(aggAge);

        //2.3、计算平均薪资
        AvgAggregationBuilder balance = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balance);

        searchRequest.source(searchSourceBuilder);

        //3、执行查询
        SearchResponse search = client.search(searchRequest, GuliamllElasticSearchConfig.COMMON_OPTIONS);

        //4、分析查询结果
        SearchHits hits = search.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            Account account = JSONObject.parseObject(hit.getSourceAsString(), Account.class);
            System.out.println(account.toString());
        }

        Aggregations aggregations = search.getAggregations();
        Terms aggAge1 = aggregations.get("aggAge");
        for (Terms.Bucket bucket : aggAge1.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("人员年纪趋势"+keyAsString+"==>"+bucket.getDocCount());
        }
        Avg balanceAvg = aggregations.get("balanceAvg");
        System.out.println("平均薪资: "+balanceAvg.getValue());

    }
}
