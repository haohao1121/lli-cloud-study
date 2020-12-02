package com.sky.lli.distributed.lock.zookeeper;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;

/**
 * zk分布式锁接口
 *
 * @author lihao
 * @date 2020-12-01
 */
public interface ZookeeperDistributedLock {

    /**
     * 获取共享可重入锁
     *
     * @param lockPath 锁路径
     *
     * @return InterProcessMutex
     */
    InterProcessMutex getInterProcessMutex(String lockPath);

    /**
     * 获取共享锁
     *
     * @param lockPath 锁路径
     *
     * @return InterProcessSemaphoreMutex
     */
    InterProcessSemaphoreMutex getInterProcessSemaphoreMutex(String lockPath);

    /**
     * 获取共享可重入读写锁
     *
     * @param lockPath 锁路径
     *
     * @return InterProcessReadWriteLock
     */
    InterProcessReadWriteLock getInterProcessReadWriteLock(String lockPath);
}
