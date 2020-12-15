package com.example.springcloud.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.context.annotation.Configuration;

//@Slf4j
//@Configuration
//@RocketMQMessageListener(topic = "springboot-rocketmq",consumerGroup = "my-group")
//public class ConsumerConfig implements RocketMQListener<String> {
//    @Override
//    public void onMessage(String s) {
//        log.info("消费消息成功");
//    }
//}
