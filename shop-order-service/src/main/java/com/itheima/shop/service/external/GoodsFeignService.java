package com.itheima.shop.service.external;

import com.itheima.entity.Result;
import com.itheima.shop.pojo.TradeGoods;
import com.itheima.shop.pojo.TradeGoodsNumberLog;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "shop-goods-service")
@Component
public interface GoodsFeignService {

    @GetMapping(value = "/GoodsControl/findOne")
    public TradeGoods findOne(@RequestParam("goodsId") Long goodsId);

    @PostMapping(value = "/GoodsControl/reduceGoodsNum")
    public Result reduceGoodsNum(@RequestBody TradeGoodsNumberLog tradeGoodsNumberLog);

}
