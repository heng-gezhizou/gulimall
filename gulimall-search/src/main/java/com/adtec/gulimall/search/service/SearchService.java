package com.adtec.gulimall.search.service;

import com.adtec.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface SearchService {

    /**
     * 商品上架，将商品信息保存到es中
     * @param skuEsModel
     */
    Boolean productUp(List<SkuEsModel> skuEsModel) throws IOException;
}
