package com.itheima.shop;

import com.itheima.entity.Result;
import com.itheima.shop.pojo.TradeGoods;
import com.itheima.shop.pojo.TradeGoodsNumberLog;
import com.itheima.shop.service.IGoodsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GoodsApplication.class)
public class GoodsTest {
    @Autowired
    private IGoodsService iGoodsService;
    @Test
    public void test(){
        TradeGoods goods = iGoodsService.findOne(345959443973935104L);
        System.out.println(goods);
    }
    @Test
    public void test2(){
        TradeGoodsNumberLog tradeGoodsNumberLog = new TradeGoodsNumberLog();
        tradeGoodsNumberLog.setOrderId(535175509206630400L);
        tradeGoodsNumberLog.setGoodsId(345959443973935104L);
        tradeGoodsNumberLog.setGoodsNumber(1);
        Result result = iGoodsService.reduceGoodsNum(tradeGoodsNumberLog);
        System.out.println(result);
    }
}
