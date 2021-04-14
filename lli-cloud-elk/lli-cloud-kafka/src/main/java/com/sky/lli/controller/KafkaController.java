package com.sky.lli.controller;

import com.sky.lli.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/12/09
 */

@RestController
@RequestMapping("kafka")
public class KafkaController {

    private final KafkaProducer kafkaProducer;

    @Autowired
    public KafkaController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @GetMapping("testSend")
    public void sendMsg() {
        int num = 10;
        for (int i = 0; i < num; i++) {
            this.kafkaProducer.send("this is test" + i + " msg");
        }
    }

}
