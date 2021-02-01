package com.sky.lli;

import com.alibaba.fastjson.JSON;
import com.sky.lli.model.DemoModel;
import com.sky.lli.model.enums.RabbitEnum;
import com.sky.lli.util.rabbitmq.RabbitUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RabbitmqApplication.class)
public class RabbitmqApplicationTests {

    @Autowired
    private RabbitUtil rabbitUtil;


    @Test
    public void testSendMq() {
        DemoModel obj = DemoModel.builder().age(11).id(UUID.randomUUID().toString()).name("lihao").build();
        this.rabbitUtil.sendToFanoutQueue(RabbitEnum.MQ_FANOUT_TETS.getExchange(), RabbitEnum.MQ_FANOUT_TETS.getQueueName(), JSON.toJSONString(obj));
    }

}
