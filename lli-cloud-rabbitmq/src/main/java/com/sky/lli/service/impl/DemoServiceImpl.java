package com.sky.lli.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.lli.model.DemoModel;
import com.sky.lli.model.enums.RabbitEnum;
import com.sky.lli.service.DemoService;
import com.sky.lli.util.rabbitmq.RabbitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述：
 * CLASSPATH: com.sky.lli.service.impl.DemoServiceImpl
 * VERSION:   1.0
 * DATE: 2019-04-24
 *
 * @author lihao
 */

@Service
public class DemoServiceImpl implements DemoService {

    private final RabbitUtil rabbitUtil;

    @Autowired
    public DemoServiceImpl(RabbitUtil rabbitUtil) {
        this.rabbitUtil = rabbitUtil;
    }

    /**
     * Date 2019-04-24
     * Author lihao
     * 方法说明: 测试发送数据
     *
     * @param obj 数据
     */
    @Override
    public void testSend(DemoModel obj) {
        this.rabbitUtil.sendToDirectQueue(RabbitEnum.MQ_DIRECT_TETS.getExchange(), RabbitEnum.MQ_DIRECT_TETS.getQueueName(), RabbitEnum.MQ_DIRECT_TETS.getRoutingKey(), JSON.toJSONString(obj));
        this.rabbitUtil.sendToTopicQueue(RabbitEnum.MQ_TOPIC_TETS.getExchange(), RabbitEnum.MQ_TOPIC_TETS.getQueueName(), RabbitEnum.MQ_TOPIC_TETS.getRoutingKey(), JSON.toJSONString(obj));
    }
}
