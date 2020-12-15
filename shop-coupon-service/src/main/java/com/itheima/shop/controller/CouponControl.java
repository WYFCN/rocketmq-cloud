package com.itheima.shop.controller;

import com.itheima.entity.Result;
import com.itheima.shop.pojo.TradeCoupon;
import com.itheima.shop.service.ICouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CouponControl {
    @Autowired
    private ICouponService iCouponService;
    @GetMapping(value = "/CouponControl/findOne")
    @ResponseBody
    public TradeCoupon findOne(@RequestParam Long couponId){
        return iCouponService.findOne(couponId);
    }
    @PostMapping(value = "/CouponControl/updateCouponStatus")
    @ResponseBody
    public Result updateCouponStatus(@RequestBody TradeCoupon coupon){
        return iCouponService.updateCouponStatus(coupon);
    }
}
