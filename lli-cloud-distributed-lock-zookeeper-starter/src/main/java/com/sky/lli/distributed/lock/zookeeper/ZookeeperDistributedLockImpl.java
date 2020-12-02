package com.sky.lli.distributed.lock.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;

/**
 * zk分布式锁接口实现
 *
 * @author lihao
 * @date 2020-12-01
 */
public class ZookeeperDistributedLockImpl implements ZookeeperDistributedLock {

    private CuratorFramework curatorFramework;

    public ZookeeperDistributedLockImpl(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    @Override
    public InterProcessMutex getInterProcessMutex(String lockPath) {
        return new InterProcessMutex(curatorFramework, lockPath);
    }

    @Override
    public InterProcessSemaphoreMutex getInterProcessSemaphoreMutex(String lockPath) {
        return new InterProcessSemaphoreMutex(curatorFramework, lockPath);
    }

    @Override
    public InterProcessReadWriteLock getInterProcessReadWriteLock(String lockPath) {
        return new InterProcessReadWriteLock(curatorFramework, lockPath);
    }
}
