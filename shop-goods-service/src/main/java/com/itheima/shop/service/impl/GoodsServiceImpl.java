package com.itheima.shop.service.impl;

import com.itheima.constant.ShopCode;
import com.itheima.entity.Result;
import com.itheima.exception.CastException;
import com.itheima.shop.mapper.TradeGoodsMapper;
import com.itheima.shop.mapper.TradeGoodsNumberLogMapper;
import com.itheima.shop.pojo.TradeGoods;
import com.itheima.shop.pojo.TradeGoodsNumberLog;
import com.itheima.shop.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private TradeGoodsMapper tradeGoodsMapper;
    @Autowired
    private TradeGoodsNumberLogMapper tradeGoodsNumberLogMapper;
    @Override
    public TradeGoods findOne(Long goodsId) {
        if(goodsId==null){
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        return tradeGoodsMapper.selectByPrimaryKey(goodsId);
    }

    @Override
    public Result reduceGoodsNum(TradeGoodsNumberLog goodsNumberLog) {
        if (goodsNumberLog == null ||
                goodsNumberLog.getGoodsNumber() == null ||
                goodsNumberLog.getOrderId() == null ||
                goodsNumberLog.getGoodsNumber() == null ||
                goodsNumberLog.getGoodsNumber().intValue() <= 0) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        TradeGoods goods = tradeGoodsMapper.selectByPrimaryKey(goodsNumberLog.getGoodsId());
        if(goods.getGoodsNumber()<goodsNumberLog.getGoodsNumber()){
            // 库存不足
            CastException.cast(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH);
        }
        //减库存
        goods.setGoodsNumber(goods.getGoodsNumber()-goodsNumberLog.getGoodsNumber());
        tradeGoodsMapper.updateByPrimaryKey(goods);

        //记录库存操作记录
        goodsNumberLog.setGoodsNumber(-goodsNumberLog.getGoodsNumber());
        goodsNumberLog.setLogTime(new Date());
        tradeGoodsNumberLogMapper.insert(goodsNumberLog);
        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
    }
}
