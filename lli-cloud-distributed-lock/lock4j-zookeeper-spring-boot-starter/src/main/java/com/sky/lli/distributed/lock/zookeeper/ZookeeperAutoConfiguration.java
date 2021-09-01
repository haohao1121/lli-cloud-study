package com.sky.lli.distributed.lock.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述: zk分布式锁
 *
 * @author lihao
 */
@Configuration
@ConditionalOnClass(ZookeeperDistributedLock.class)
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperAutoConfiguration {

    /**
     * 初始休眠时间
     */
    private static final int BASE_SLEEP_TIME_MS = 1000;

    private final ZookeeperProperties zookeeperProperties;

    @Autowired
    public ZookeeperAutoConfiguration(ZookeeperProperties zookeeperProperties) {
        this.zookeeperProperties = zookeeperProperties;
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(CuratorFramework.class)
    public CuratorFramework curatorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS,
                zookeeperProperties.getCuratorRetryCount());
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zookeeperProperties.getAddrs())
                .sessionTimeoutMs(zookeeperProperties.getCuratorSessionTimeoutMs())
                .connectionTimeoutMs(zookeeperProperties.getCuratorConnectionTimeoutMs())
                .retryPolicy(retryPolicy).build();
        curatorFramework.start();
        return curatorFramework;
    }

    @Bean
    public ZookeeperDistributedLock zookeeperDistributedLock() {
        return new ZookeeperDistributedLockImpl(curatorFramework(),zookeeperProperties);
    }

}
