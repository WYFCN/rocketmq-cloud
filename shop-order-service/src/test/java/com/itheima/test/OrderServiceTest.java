package com.itheima.test;

import com.itheima.entity.Result;
import com.itheima.shop.OrderApplication;
import com.itheima.shop.pojo.TradeGoods;
import com.itheima.shop.pojo.TradeGoodsNumberLog;
import com.itheima.shop.pojo.TradeOrder;
import com.itheima.shop.pojo.TradeUser;
import com.itheima.shop.service.IOrderService;
import com.itheima.shop.service.external.GoodsFeignService;
import com.itheima.shop.service.external.UserFeignService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApplication.class)
public class OrderServiceTest {
    @Autowired
    private IOrderService orderService;
    @Autowired
    private GoodsFeignService goodsFeignService;
    @Autowired
    private UserFeignService userFeignService;
    @Test
    public void confirmOrder() throws IOException {
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
        orderService.confirmOrder(order);

        System.in.read();
    }
    @Test
    public void test(){
        TradeUser one = userFeignService.findOne(345963634385633280L);
        System.out.println(one.toString());
    }
    @Test
    public void test2(){
        TradeOrder order = new TradeOrder();
        order.setGoodsId(345959443973935104L);
        TradeGoods one = goodsFeignService.findOne(order.getGoodsId());
        System.out.println(one);
    }
    @Test
    public void test3(){
        TradeGoodsNumberLog tradeGoodsNumberLog = new TradeGoodsNumberLog();
        tradeGoodsNumberLog.setOrderId(535175509206630400L);
        tradeGoodsNumberLog.setGoodsId(345959443973935104L);
        tradeGoodsNumberLog.setGoodsNumber(1);
        Result result = goodsFeignService.reduceGoodsNum(tradeGoodsNumberLog);
        System.out.println(result);
    }
}
