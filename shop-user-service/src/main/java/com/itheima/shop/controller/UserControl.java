package com.itheima.shop.controller;

import com.itheima.entity.Result;
import com.itheima.shop.mapper.TradeUserMapper;
import com.itheima.shop.pojo.TradeUser;
import com.itheima.shop.pojo.TradeUserMoneyLog;
import com.itheima.shop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserControl {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private TradeUserMapper tradeUserMapper;
    @GetMapping(value = "/UserControl/findOne")
    @ResponseBody
    public TradeUser findOne(@RequestParam Long userId){
        return iUserService.findOne(userId);
    }
    @PostMapping(value = "/UserControl/updateMoneyPaid")
    @ResponseBody
    public Result updateMoneyPaid(@RequestBody TradeUserMoneyLog userMoneyLog){
        return iUserService.updateMoneyPaid(userMoneyLog);
    }
    @GetMapping(value = "/product123")
    @ResponseBody
    public TradeUser getts(){
        return tradeUserMapper.gets();
    }
}
