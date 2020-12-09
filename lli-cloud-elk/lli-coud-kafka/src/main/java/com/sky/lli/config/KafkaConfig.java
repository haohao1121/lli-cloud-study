package com.sky.lli.config;

import com.sky.lli.config.enums.TopicEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import javax.annotation.PostConstruct;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/12/09
 */

@Slf4j
@Configuration
public class KafkaConfig {

    private KafkaAdmin kafkaAdmin;

    @Autowired
    public KafkaConfig(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    /**
     * 此种@Bean的方式，如果topic的名字相同，那么会覆盖以前的那个
     * 修改后|分区数量会变成11个 注意分区数量只能增加不能减少
     */
    @Bean
    public NewTopic initialTopic() {
        return new NewTopic("topic.quick.initial", 12, (short) 1);
    }

    /**
     * 初始化枚举中的topic
     */
    @PostConstruct
    public void initTopic() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfig())) {
            adminClient.createTopics(TopicEnum.listAllTopic());
        } catch (Exception e) {
            log.error("init topic error:", e);
        }
    }
}
