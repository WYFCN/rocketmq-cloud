package com.itheima.shop.service.impl;

import com.itheima.constant.ShopCode;
import com.itheima.entity.Result;
import com.itheima.exception.CastException;
import com.itheima.shop.mapper.TradeCouponMapper;
import com.itheima.shop.pojo.TradeCoupon;
import com.itheima.shop.service.ICouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouponServiceImpl implements ICouponService {
    @Autowired
    TradeCouponMapper tradeCouponMapper;
    @Override
    public TradeCoupon findOne(Long couponId) {
        if(couponId==null){
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        TradeCoupon tradeCoupon = tradeCouponMapper.selectByPrimaryKey(couponId);
        return tradeCoupon;
    }

    @Override
    public Result updateCouponStatus(TradeCoupon coupon) {
        if(coupon==null||coupon.getCouponId()==null){
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        tradeCouponMapper.updateByPrimaryKey(coupon);
        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
    }
}
