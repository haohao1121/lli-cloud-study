package com.sky.lli.model.enums;

import lombok.Getter;

/**
 * @author lihao
 */

@Getter
public enum RabbitEnum {


    //================================测试枚举===================================
    //direct测试
    MQ_DIRECT_TETS("testdirect", "testdir", "test.dir"),
    //topic测试
    MQ_TOPIC_TETS("testtopic", "testtopic", "test.topic"),
    //fanout测试
    MQ_FANOUT_TETS("testfanout", "testfanout", "test.fanout");


    RabbitEnum(String exchange, String queueName, String routingKey) {
        this.exchange = exchange;
        this.queueName = queueName;
        this.routingKey = routingKey;
    }

    private String exchange;
    private String queueName;
    private String routingKey;
}
