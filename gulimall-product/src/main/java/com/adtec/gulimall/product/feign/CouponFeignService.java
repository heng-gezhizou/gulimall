package com.adtec.gulimall.product.feign;

import com.adtec.common.to.SkuFullReductionTo;
import com.adtec.common.to.SpuBoundTo;
import com.adtec.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    /**
     * feigh将spuBoundTo数据转为json发送出去，被调用的接口可以将该json转为SpuBoundsEntity，
     * 只有json数据模型是兼容的，双方服务无需使用同一个to
     * @param spuBoundTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBound(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveReduction(@RequestBody SkuFullReductionTo skuFullReductionTo);
}
