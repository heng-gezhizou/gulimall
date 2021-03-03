package com.adtec.gulimall.search.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SearchParam {
    private String keyword;//关键字查询
    private Long catalog3Id;//3级分类id查询

    /**
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
     */
    private String sort;//排序条件

    /**
     * 好多的过滤条件
     * hasStock(是否有货)、skuPrice区间、brandId、catalog3Id、attrs
     * hasStock=0/1
     * skuPrice=1_500
     */
    private Integer hasStock;//是否有货
    private String skuPrice;//价格区间
    private List<Long> brandId;//品牌id


    private List<String> attrs;//按照品牌属性进行筛选
    private Integer pageNum = 1;//页码
}
