package com.itheima.shop.service.impl;

import com.alibaba.fastjson.JSON;
import com.itheima.constant.ShopCode;
import com.itheima.entity.MQEntity;
import com.itheima.entity.Result;
import com.itheima.exception.CastException;
import com.itheima.shop.mapper.TradeOrderMapper;
import com.itheima.shop.pojo.*;
import com.itheima.shop.service.IOrderService;
import com.itheima.shop.service.external.CouponFeignService;
import com.itheima.shop.service.external.GoodsFeignService;
import com.itheima.shop.service.external.UserFeignService;
import com.itheima.utils.IDWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public class OrderServiceImpl implements IOrderService {

    @Value("${mq.order.topic}")
    private String topic;
    @Value("${mq.order.tag.cancel}")
    private String tag;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private TradeOrderMapper tradeOrderMapper;
    @Resource
    private GoodsFeignService goodsFeignService;
    @Resource
    private UserFeignService userFeignService;
    @Resource
    private CouponFeignService couponFeignService;
    @Override
    public Result confirmOrder(TradeOrder order) {
        //1.校验订单
        checkOrder(order);
        //2.生成预订单
        Long orderId = savePreOrder(order);
        try {
            //3.扣减库存
            reduceGoodsNum(order);
            //4.扣减优惠券
            updateCouponStatus(order);
            //5.使用余额
            reduceMoneyPaid(order);
            //模拟异常抛出
            CastException.cast(ShopCode.SHOP_FAIL);
            //6.确认订单
            updateOrderStatus(order);
            //7.返回成功状态
            return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
        } catch (Exception e) {
            //1.确认订单失败,发送消息
            MQEntity mqEntity = new MQEntity();
            mqEntity.setOrderId(orderId);
            mqEntity.setUserId(order.getUserId());
            mqEntity.setUserMoney(order.getMoneyPaid());
            mqEntity.setGoodsId(order.getGoodsId());
            mqEntity.setGoodsNum(order.getGoodsNumber());
            mqEntity.setCouponId(order.getCouponId());

            //2.发送订单确认失败消息
            try {
                sendCancelOrder(topic,tag,order.getOrderId().toString(), JSON.toJSONString(mqEntity));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return new Result(ShopCode.SHOP_FAIL.getSuccess(),ShopCode.SHOP_FAIL.getMessage());
        }
    }

    /**
     * 发送订单确认失败消息
     * @param topic
     * @param tag
     * @param keys
     * @param body
     */
    private void sendCancelOrder(String topic, String tag, String keys, String body) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Message message = new Message(topic,tag,keys,body.getBytes());
        rocketMQTemplate.getProducer().send(message);
    }

    /**
     * 确认订单
     * @param order
     */
    private void updateOrderStatus(TradeOrder order) {
        order.setOrderStatus(ShopCode.SHOP_ORDER_CONFIRM.getCode());
        order.setPayStatus(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        order.setConfirmTime(new Date());
        int r = tradeOrderMapper.updateByPrimaryKey(order);
        if(r<=0){
            CastException.cast(ShopCode.SHOP_ORDER_CONFIRM_FAIL);
        }
        log.info("订单:"+order.getOrderId()+"确认订单成功");

    }

    /**
     * 扣减余额
     * @param order
     */
    private void reduceMoneyPaid(TradeOrder order) {
        if(order.getMoneyPaid()!=null && order.getMoneyPaid().compareTo(BigDecimal.ZERO)==1){
            TradeUserMoneyLog tradeUserMoneyLog = new TradeUserMoneyLog();
            tradeUserMoneyLog.setOrderId(order.getOrderId());
            tradeUserMoneyLog.setUserId(order.getUserId());
            tradeUserMoneyLog.setUseMoney(order.getMoneyPaid());
            tradeUserMoneyLog.setMoneyLogType(ShopCode.SHOP_USER_MONEY_PAID.getCode());
            Result result = userFeignService.updateMoneyPaid(tradeUserMoneyLog);
            if (result.getSuccess().equals(ShopCode.SHOP_FAIL.getSuccess())) {
                CastException.cast(ShopCode.SHOP_USER_MONEY_REDUCE_FAIL);
            }
            log.info("订单:"+order.getOrderId()+",扣减余额成功");
        }
    }

    /**
     * 扣减优惠券
     * @param order
     */
    private void updateCouponStatus(TradeOrder order) {
        if(order.getCouponId()!=null){
            TradeCoupon coupon = couponFeignService.findOne(order.getCouponId());
            coupon.setOrderId(order.getOrderId());
            coupon.setIsUsed(ShopCode.SHOP_COUPON_ISUSED.getCode());
            coupon.setUsedTime(new Date());
            //更新优惠券状态
            Result result = couponFeignService.updateCouponStatus(coupon);
            if(result.getSuccess().equals(ShopCode.SHOP_FAIL.getSuccess())){
                CastException.cast(ShopCode.SHOP_COUPON_USE_FAIL);
            }
            log.info("订单:"+order.getOrderId()+",使用优惠券");
        }
    }
    /**
     * 扣减库存
     * @param order
     */
    private void reduceGoodsNum(TradeOrder order) {
        TradeGoodsNumberLog tradeGoodsNumberLog = new TradeGoodsNumberLog();
        tradeGoodsNumberLog.setOrderId(order.getOrderId());
        tradeGoodsNumberLog.setGoodsId(order.getGoodsId());
        tradeGoodsNumberLog.setGoodsNumber(order.getGoodsNumber());
        Result result = goodsFeignService.reduceGoodsNum(tradeGoodsNumberLog);
        if(result.getSuccess().equals(ShopCode.SHOP_FAIL.getSuccess())){
            CastException.cast(ShopCode.SHOP_REDUCE_GOODS_NUM_FAIL);
        }
        log.info("订单:"+order.getOrderId()+"扣减库存成功");
    }

    /**
     * 生成预订单
     * @param order
     * @return
     */
    private Long savePreOrder(TradeOrder order) {
        //1.设置订单状态为不可见
        order.setOrderStatus(ShopCode.SHOP_ORDER_NO_CONFIRM.getCode());
        //2.订单ID
        long orderId = IDWorker.nextId();
        order.setOrderId(orderId);
        //3.核算运费是否正确
        BigDecimal shippingFee = calculateShippingFee(order.getOrderAmount());
        if(order.getShippingFee().compareTo(shippingFee)!=0){
            CastException.cast(ShopCode.SHOP_ORDER_SHIPPINGFEE_INVALID);
        }
        //4.计算订单总价格是否正确
        BigDecimal orderAmount = order.getGoodsPrice().multiply(new BigDecimal(order.getGoodsNumber()));
        orderAmount.add(shippingFee);
        if(order.getOrderAmount().compareTo(orderAmount)!=0){
            CastException.cast(ShopCode.SHOP_ORDERAMOUNT_INVALID);
        }
        //5.判断用户是否使用余额
        BigDecimal moneyPaid = order.getMoneyPaid();
        if(moneyPaid!=null){
            //5.1 订单中余额死否合法
            int r = moneyPaid.compareTo(BigDecimal.ZERO);
            //余额小于0
            if(r==-1){
                CastException.cast(ShopCode.SHOP_MONEY_PAID_LESS_ZERO);
            }
            //余额大于0
            if(r==1){
                TradeUser user = userFeignService.findOne(order.getUserId());
                if(moneyPaid.compareTo(new BigDecimal(user.getUserMoney()))==1){
                    CastException.cast(ShopCode.SHOP_MONEY_PAID_INVALID);
                }
            }
        }else{
            order.setMoneyPaid(BigDecimal.ZERO);
        }
        //6.判断用户是否使用优惠券
        Long couponId = order.getCouponId();
        if(couponId!=null){
            //6.1 判断优惠券是否存在
            TradeCoupon coupon = couponFeignService.findOne(couponId);
            if(coupon==null){
                CastException.cast(ShopCode.SHOP_COUPON_NO_EXIST);
            }
            //6.2 判断优惠券是否已经被使用
            if(coupon.getIsUsed().intValue()==ShopCode.SHOP_COUPON_ISUSED.getCode()){
                CastException.cast(ShopCode.SHOP_COUPON_ISUSED);
            }
            order.setCouponPaid(coupon.getCouponPrice());
        }else{
            order.setCouponPaid(BigDecimal.ZERO);
        }
        //7.核算订单支付金额 订单金额-余额-优惠券金额
        BigDecimal payAmount = order.getOrderAmount().subtract(order.getMoneyPaid()).subtract(order.getCouponPaid());
        order.setPayAmount(payAmount);
        //8.设置下单时间
        order.setAddTime(new Date());
        //9.保存订单到数据库
        tradeOrderMapper.insert(order);
        return orderId;
    }

    /**
     * 计算订单运费
     * @param orderAmount
     */
    private BigDecimal calculateShippingFee(BigDecimal orderAmount) {
        if(orderAmount.compareTo(new BigDecimal(100))==1){
            return BigDecimal.ZERO;
        }else{
            return new BigDecimal(10);
        }
    }

    /**
     * 校验订单
     * @param order
     */
    private void checkOrder(TradeOrder order) {
        //1.校验订单是否存在
        if(order==null){
            CastException.cast(ShopCode.SHOP_ORDER_INVALID);
        }
        //2. 校验订单中的商品是否存在
        Long goodId = order.getGoodsId();
        TradeGoods goods = goodsFeignService.findOne(goodId);
        System.out.println(goods);
        if(goods==null){
            CastException.cast(ShopCode.SHOP_GOODS_NO_EXIST);
        }
        //3.校验下单用户是否存在
        TradeUser user = userFeignService.findOne(order.getUserId());
        if(user==null){
            CastException.cast(ShopCode.SHOP_USER_NO_EXIST);
        }
        //4.校验商品单价是否合法
        if(order.getPayAmount().compareTo(goods.getGoodsPrice())!=0){
            CastException.cast(ShopCode.SHOP_GOODS_PRICE_INVALID);
        }
        //5.订单数量是否合法
        if(order.getGoodsNumber()>=goods.getGoodsNumber()){
            CastException.cast(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH);
        }
        log.info("校验订单通过");
    }
}
