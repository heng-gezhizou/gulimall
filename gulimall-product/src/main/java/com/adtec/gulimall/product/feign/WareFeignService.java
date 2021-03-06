package com.adtec.gulimall.product.feign;

import com.adtec.common.to.HasStockTo;
import com.adtec.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasstock")
    R hasStock(@RequestBody List<Long> skuIds);

}
