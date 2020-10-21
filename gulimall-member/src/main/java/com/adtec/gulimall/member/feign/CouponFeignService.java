package com.adtec.gulimall.member.feign;

import com.adtec.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    //远程调用gulimall-coupon的接口
    @RequestMapping("/coupon/coupon/member")
    public R memberCoupon();
}
