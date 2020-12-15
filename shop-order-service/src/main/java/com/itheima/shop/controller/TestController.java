package com.itheima.shop.controller;

import com.itheima.entity.Result;
import com.itheima.shop.pojo.TradeOrder;
import com.itheima.shop.service.IOrderService;
import com.itheima.shop.service.external.GoodsFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Controller
public class TestController {
    @Autowired
    private IOrderService orderService;
    @GetMapping("/test")
    @ResponseBody
    public Result test(){
        Long coupouId = 345988230098857984L;
        Long goodsId = 345959443973935104L;
        Long userId = 345963634385633280L;

        TradeOrder order = new TradeOrder();
        order.setGoodsId(goodsId);
        order.setUserId(userId);
        order.setCouponId(coupouId);
        order.setAddress("北京");
        order.setGoodsNumber(1);
        order.setGoodsPrice(new BigDecimal(1000));
        order.setShippingFee(BigDecimal.ZERO);
        order.setOrderAmount(new BigDecimal(1000));
        order.setPayAmount(new BigDecimal(1000));
        order.setMoneyPaid(new BigDecimal(100));
        return orderService.confirmOrder(order);
    }

}
