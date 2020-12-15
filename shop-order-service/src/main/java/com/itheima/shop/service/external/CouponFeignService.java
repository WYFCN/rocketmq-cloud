package com.itheima.shop.service.external;

import com.itheima.entity.Result;
import com.itheima.shop.pojo.TradeCoupon;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(value = "shop-coupon-service")
public interface CouponFeignService {
    @GetMapping(value = "/CouponControl/findOne")
    public TradeCoupon findOne(@RequestParam("couponId") Long couponId);
    @PostMapping(value = "/CouponControl/updateCouponStatus")
    public Result updateCouponStatus(@RequestBody TradeCoupon coupon);
}
