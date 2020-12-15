package com.itheima.shop.service;

import com.itheima.entity.Result;
import com.itheima.shop.pojo.TradeOrder;

public interface IOrderService {
    /**
     * 下订单接口
     * @param order
     * @return
     */
    public Result confirmOrder(TradeOrder order);
}
