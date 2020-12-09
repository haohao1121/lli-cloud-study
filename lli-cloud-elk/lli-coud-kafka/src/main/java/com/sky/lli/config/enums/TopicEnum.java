package com.sky.lli.config.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/12/09
 */

@Getter
@AllArgsConstructor
public enum TopicEnum {

    /**
     * elk topic
     */
    ELK_TOPIC("elk.topic", 50, (short) 1),

    /**
     * test topic
     */
    TEST_TOPIC("test.topic", 8, (short) 1);

    /**
     * 方法说明: 获取所有的topic
     */
    public static List<NewTopic> listAllTopic() {
        return Arrays.stream(TopicEnum.values()).map(value -> new NewTopic(value.getName(), value.getNumPartitions(),
                                                                           value.getReplicationFactor()))
                        .collect(Collectors.toList());
    }

    public final String name;
    private int numPartitions;
    private short replicationFactor;

}
