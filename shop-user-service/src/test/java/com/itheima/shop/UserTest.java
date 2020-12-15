package com.itheima.shop;

import com.itheima.shop.mapper.TradeUserMapper;
import com.itheima.shop.pojo.TradeUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
public class UserTest {
    @Autowired
    private TradeUserMapper tradeUserMapper;
    @Test
    public void test(){
        TradeUser user = tradeUserMapper.gets();
        System.out.println(user);
    }
    @Test
    public void test2(){
        TradeUser usr =tradeUserMapper.selectByPrimaryKey(345963634385633280L);
        usr.setUserMoney(900L);
        tradeUserMapper.updateByPrimaryKey(usr);
    }
}
