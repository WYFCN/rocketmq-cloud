package com.example.springcloud.springcloud.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping(value = "/producer/test")
    public String test(){
        return "我是生产者，请调用我........==！";
    }
}
