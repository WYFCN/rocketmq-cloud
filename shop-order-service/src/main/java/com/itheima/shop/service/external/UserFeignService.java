package com.itheima.shop.service.external;

import com.itheima.entity.Result;
import com.itheima.shop.pojo.TradeUser;
import com.itheima.shop.pojo.TradeUserMoneyLog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service")
@Component
public interface UserFeignService {
    @GetMapping(value = "/UserControl/findOne")
    public TradeUser findOne(@RequestParam("userId") Long userId);
    @PostMapping(value = "/UserControl/updateMoneyPaid")
    public Result updateMoneyPaid(@RequestBody TradeUserMoneyLog userMoneyLog);
    @GetMapping(value = "/product123")
    public TradeUser getts();
}
