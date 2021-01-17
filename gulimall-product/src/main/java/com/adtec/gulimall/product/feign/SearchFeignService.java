package com.adtec.gulimall.product.feign;

import com.adtec.common.to.es.SkuEsModel;
import com.adtec.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-search")
public interface SearchFeignService {
    @PostMapping("/search/save/productup")
    R productUp(@RequestBody List<SkuEsModel> skuEsModel);
}
