package com.adtec.gulimall.search.service;

import com.adtec.gulimall.search.entity.SearchParam;
import com.adtec.gulimall.search.entity.SearchResult;

public interface MallSearchService {
    public SearchResult SearchProduct(SearchParam param);
}
