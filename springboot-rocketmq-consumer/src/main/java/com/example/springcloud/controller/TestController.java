package com.example.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
public class TestController {

    @Autowired
    RestTemplate restTemplate;

    @Value("${service-url.nacos-user-srevice}")
    private String serverURL;
    @GetMapping(value = "/consumer/test")
    public String test(){
        String forObject = restTemplate.getForObject(serverURL + "/producer/test", String.class);
        log.info(forObject);
        return "消费者消费成功.....！";
    }
}
