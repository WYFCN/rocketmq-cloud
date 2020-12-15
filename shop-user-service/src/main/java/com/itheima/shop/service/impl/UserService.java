package com.itheima.shop.service.impl;

import com.itheima.constant.ShopCode;
import com.itheima.entity.Result;
import com.itheima.exception.CastException;
import com.itheima.shop.mapper.TradeUserMapper;
import com.itheima.shop.mapper.TradeUserMoneyLogMapper;
import com.itheima.shop.pojo.TradeUser;
import com.itheima.shop.pojo.TradeUserMoneyLog;
import com.itheima.shop.pojo.TradeUserMoneyLogExample;
import com.itheima.shop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class UserService implements IUserService {
    @Autowired
    private TradeUserMapper tradeUserMapper;
    @Autowired
    TradeUserMoneyLogMapper tradeUserMoneyLogMapper;
    @Override
    public TradeUser findOne(Long userId) {
        if(userId==null){
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        return tradeUserMapper.selectByPrimaryKey(userId);
    }

    @Override
    public Result updateMoneyPaid(TradeUserMoneyLog userMoneyLog) {
        //1.校验参数是否合法
        if(userMoneyLog==null ||
                userMoneyLog.getUserId()==null ||
                userMoneyLog.getOrderId()==null ||
                userMoneyLog.getUseMoney().compareTo(BigDecimal.ZERO)<=0 ||
                userMoneyLog.getUseMoney()==null){
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        //2.查询订单余额使用日志
        TradeUserMoneyLogExample userMoneyLogExample = new TradeUserMoneyLogExample();
        TradeUserMoneyLogExample.Criteria criteria = userMoneyLogExample.createCriteria();
        criteria.andOrderIdEqualTo(userMoneyLog.getOrderId());
        criteria.andUserIdEqualTo(userMoneyLog.getUserId());
        int r = tradeUserMoneyLogMapper.countByExample(userMoneyLogExample);
        //3.扣减余额
        if(userMoneyLog.getMoneyLogType().intValue()==ShopCode.SHOP_USER_MONEY_PAID.getCode().intValue()){
            if(r>0){
                //已经付款
                CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY);
            }
            TradeUser tradeUser = tradeUserMapper.selectByPrimaryKey(userMoneyLog.getUserId());
            tradeUser.setUserMoney(new BigDecimal(tradeUser.getUserMoney()).subtract(userMoneyLog.getUseMoney()).longValue());
            tradeUserMapper.updateByPrimaryKey(tradeUser);
        }
        //4.回退余额
        if(userMoneyLog.getMoneyLogType().intValue()==ShopCode.SHOP_USER_MONEY_PAID.getCode().intValue()){
            if(r<0){
                //已经付款
                CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY);
            }
            //防止多次退款
            TradeUserMoneyLogExample userMoneyLogExample2 = new TradeUserMoneyLogExample();
            TradeUserMoneyLogExample.Criteria criteria1 = userMoneyLogExample2.createCriteria();
            criteria1.andOrderIdEqualTo(userMoneyLog.getOrderId());
            criteria1.andUserIdEqualTo(userMoneyLog.getUserId());
            criteria.andMoneyLogTypeEqualTo(ShopCode.SHOP_USER_MONEY_REFUND.getCode());
            int r2 = tradeUserMoneyLogMapper.countByExample(userMoneyLogExample2);
            if(r2>0){
                CastException.cast(ShopCode.SHOP_USER_MONEY_REFUND_ALREADY);
            }
            //退款
            TradeUser tradeUser = tradeUserMapper.selectByPrimaryKey(userMoneyLog.getUserId());
            tradeUser.setUserMoney(new BigDecimal(tradeUser.getUserMoney()).add(userMoneyLog.getUseMoney()).longValue());
            tradeUserMapper.updateByPrimaryKey(tradeUser);
        }
        //5.记录订单余额使用日志
        userMoneyLog.setCreateTime(new Date());
        tradeUserMoneyLogMapper.insert(userMoneyLog);
        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
    }
}
