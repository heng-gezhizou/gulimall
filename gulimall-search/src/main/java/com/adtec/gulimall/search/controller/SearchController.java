package com.adtec.gulimall.search.controller;

import com.adtec.common.exception.BizCodeEnum;
import com.adtec.common.to.es.SkuEsModel;
import com.adtec.common.utils.R;
import com.adtec.gulimall.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/search/save")
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("/productup")
    public R productUp(@RequestBody List<SkuEsModel> skuEsModel) {
        Boolean b = false;
        try {
            b = searchService.productUp(skuEsModel);
        }catch (Exception e){
            log.error("SearchController商品上架错误：{}",e);
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }

        if(!b){
            return R.ok();
        }else {
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
    }

}
