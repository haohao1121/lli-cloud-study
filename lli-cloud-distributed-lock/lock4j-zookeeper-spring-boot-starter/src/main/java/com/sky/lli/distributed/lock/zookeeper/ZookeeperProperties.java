package com.sky.lli.distributed.lock.zookeeper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * zk配置信息
 *
 * @author lihao
 * @date 2020-12-01
 */
@Data
@ConfigurationProperties(prefix = "lock4j-zookeeper")
public class ZookeeperProperties {

    /**
     * zk地址，集群使用,分割：127.0.0.1:2181,127.0.0.1:3181
     */
    private String addrs = "127.0.0.1:2181";

    /**
     * 重试次数
     */
    private int curatorRetryCount = 5;

    /**
     * session超时时间
     */
    private int curatorSessionTimeoutMs = 60000;

    /**
     * 连接超时时间
     */
    private int curatorConnectionTimeoutMs = 5000;

    /**
     * 根路径
     */
    private String baseLockPath = "zk-lock";

}
