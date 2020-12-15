package com.itheima.shop.service;

import com.itheima.entity.Result;
import com.itheima.shop.pojo.TradeGoods;
import com.itheima.shop.pojo.TradeGoodsNumberLog;

import javax.annotation.Resource;

public interface IGoodsService {

    /**
     * 根据ID查询商品对象
     * @param goodsId
     * @return
     */
    TradeGoods findOne(Long goodsId);

    /**
     * 扣减库存
     * @param tradeGoodsNumberLog
     * @return
     */
    Result reduceGoodsNum(TradeGoodsNumberLog tradeGoodsNumberLog);
}

