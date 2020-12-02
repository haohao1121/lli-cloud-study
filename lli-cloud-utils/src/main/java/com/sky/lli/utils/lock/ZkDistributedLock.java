package com.sky.lli.utils.lock;

import com.sky.lli.distributed.lock.zookeeper.ZookeeperDistributedLock;
import com.sky.lli.utils.exception.ExceptionEnum;
import com.sky.lli.utils.exception.UtilsException;
import com.sky.lli.utils.thread.NativeAsyncTaskExecutePool;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.utils.ZKPaths;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/12/02
 */

@Slf4j
@Component
public class ZkDistributedLock {

    /**
     * 基础节点根路径
     */
    private static final String BASE_LOCK_PATH = "zk-lock";

    //    ZKPaths.makePath(path1,path)

    private final ZookeeperDistributedLock zookeeperDistributedLock;
    private final NativeAsyncTaskExecutePool threadExecutePool;

    @Autowired
    public ZkDistributedLock(ZookeeperDistributedLock zookeeperDistributedLock,
                             NativeAsyncTaskExecutePool threadExecutePool) {
        this.zookeeperDistributedLock = zookeeperDistributedLock;
        this.threadExecutePool = threadExecutePool;
    }

    /**
     * 在锁里运行任务
     *
     * @param lockPath 路径
     * @param callable 获取资源后的操作
     * @param waitTime 锁等待时间秒
     */
    public <T> T runWithLockSync(String lockPath, int waitTime, Callable<T> callable) {
        //路径处理
        lockPath = ZKPaths.makePath(BASE_LOCK_PATH, lockPath);
        InterProcessMutex lock = zookeeperDistributedLock.getInterProcessMutex(lockPath);

        try {
            boolean acquire = lock.acquire(waitTime, TimeUnit.SECONDS);
            if (!acquire) {
                log.error("加锁超时,时间[{}]", waitTime);
                throw new UtilsException(ExceptionEnum.SYS_REDIS_LOCK_ERROR);
            }
            return callable.call();
        } catch (Exception e) {
            log.error("加锁运行失败", e);
            throw new UtilsException(ExceptionEnum.SYS_REDIS_LOCK_ERROR);
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                log.error("释放锁异常", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 在锁里运行任务(异步执行)
     *
     * @param lockPath 路径
     * @param callable 获取资源后的操作
     */
    public void runWithLockAsync(String lockPath, Callable<T> callable) {
        lockPath = ZKPaths.makePath(BASE_LOCK_PATH, lockPath);
        InterProcessMutex lock = zookeeperDistributedLock.getInterProcessMutex(lockPath);
        try {
            lock.acquire();

            Objects.requireNonNull(this.threadExecutePool.getAsyncExecutor()).execute(() -> {
                try {
                    callable.call();
                } catch (Exception e) {
                    log.error("加锁异步运行失败", e);
                }
            });
        } catch (Exception e) {
            log.error("加锁运行失败", e);
            throw new UtilsException(ExceptionEnum.SYS_REDIS_LOCK_ERROR);
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                log.error("释放锁异常", e);
                e.printStackTrace();
            }
        }
    }
}
