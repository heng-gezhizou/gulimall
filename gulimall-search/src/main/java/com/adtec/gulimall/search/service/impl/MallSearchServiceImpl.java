package com.adtec.gulimall.search.service.impl;

import com.adtec.gulimall.search.config.GuliamllElasticSearchConfig;
import com.adtec.gulimall.search.entity.SearchParam;
import com.adtec.gulimall.search.entity.SearchResult;
import com.adtec.gulimall.search.service.MallSearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public SearchResult SearchProduct(SearchParam param) {
        SearchResult result = null;
        //构建请求DSL语句
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, GuliamllElasticSearchConfig.COMMON_OPTIONS);
            result = buildResponseResult(response,param);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }

    private SearchResult buildResponseResult(SearchResponse response,SearchParam param) {
        return null;
    }

    private SearchRequest buildSearchRequest(SearchParam param) {
        //构建DSL语句
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        /*
        过滤（按照属性，分类，库存，价格区间，品牌）
         */
        //1构建bool-query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        sourceBuilder.query(boolQuery);
        //1.1构建must-模糊查询
        if(!StringUtils.isEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }
        //1.2构建filter查询
        //1.2.1构建filter分类ID查询
        if(param.getCatalog3Id() != null){
            boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
        }
        //1.2.2构建filter品牌ID查询
        if(param.getBrandId() != null && param.getBrandId().size() > 0){
            boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }
        //1.2.3构建filter是否有库存查询
        if(param.getHasStock() != null){
            boolQuery.filter(QueryBuilders.termsQuery("hasStock",param.getHasStock() == 1));
        }
        //1.2.4构建filter价格区间查询 1_500/_500/500_
        if(!StringUtils.isEmpty(param.getSkuPrice())){
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("skuPrice");
            String price = param.getSkuPrice();
            String[] s = price.split("_");
            if(s.length == 2){
               rangeQueryBuilder.from(s[0]);
               rangeQueryBuilder.to(s[1]);
            }else if(s.length == 1){
                if(price.startsWith("_")){
                    rangeQueryBuilder.to(s[0]);
                }else if(price.endsWith("_")){
                    rangeQueryBuilder.from("-");
                }
            }
            boolQuery.filter(rangeQueryBuilder);
        }






        return null;
    }

}
