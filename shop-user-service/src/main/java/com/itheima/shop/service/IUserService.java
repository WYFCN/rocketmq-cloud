package com.itheima.shop.service;

import com.itheima.entity.Result;
import com.itheima.shop.pojo.TradeUser;
import com.itheima.shop.pojo.TradeUserMoneyLog;

public interface IUserService {
    /**
     * 通过userId查找User对象
     * @param userId
     * @return
     */
    TradeUser findOne(Long userId);

    /**
     *
     * @param userMoneyLog
     */
    Result updateMoneyPaid(TradeUserMoneyLog userMoneyLog);
}
