package com.sky.lli.distributed.lock.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.utils.ZKPaths;

/**
 * zk分布式锁接口实现
 *
 * @author lihao
 * @date 2020-12-01
 */
public class ZookeeperDistributedLockImpl implements ZookeeperDistributedLock {

    private CuratorFramework curatorFramework;
    private ZookeeperProperties zookeeperProperties;

    public ZookeeperDistributedLockImpl(CuratorFramework curatorFramework,
                                        ZookeeperProperties zookeeperProperties) {
        this.curatorFramework = curatorFramework;
        this.zookeeperProperties = zookeeperProperties;
    }

    @Override
    public InterProcessMutex getInterProcessMutex(String lockPath) {
        return new InterProcessMutex(curatorFramework, ZKPaths.makePath(zookeeperProperties.getBaseLockPath(), lockPath));
    }

    @Override
    public InterProcessSemaphoreMutex getInterProcessSemaphoreMutex(String lockPath) {
        return new InterProcessSemaphoreMutex(curatorFramework, ZKPaths.makePath(zookeeperProperties.getBaseLockPath(), lockPath));
    }

    @Override
    public InterProcessReadWriteLock getInterProcessReadWriteLock(String lockPath) {
        return new InterProcessReadWriteLock(curatorFramework, ZKPaths.makePath(zookeeperProperties.getBaseLockPath(), lockPath));
    }
}
