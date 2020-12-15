package com.itheima.shop.controller;

import com.itheima.entity.Result;
import com.itheima.shop.pojo.TradeGoods;
import com.itheima.shop.pojo.TradeGoodsNumberLog;
import com.itheima.shop.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class GoodsControl {
    @Autowired
    private IGoodsService iGoodsService;
    @GetMapping(value = "/GoodsControl/findOne")
    @ResponseBody
    public TradeGoods findOne(@RequestParam Long goodsId){
        return iGoodsService.findOne(goodsId);
    }
    @PostMapping(value = "/GoodsControl/reduceGoodsNum")
    @ResponseBody
    public Result reduceGoodsNum(@RequestBody TradeGoodsNumberLog tradeGoodsNumberLog){
        return iGoodsService.reduceGoodsNum(tradeGoodsNumberLog);
    }
}
