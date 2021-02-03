package com.sky.lli.utils.lock;

import com.sky.lli.utils.exception.ExceptionEnum;
import com.sky.lli.utils.exception.UtilsException;
import com.sky.lli.utils.thread.NativeAsyncTaskExecutePool;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Redis locker
 *
 * @author lihao
 */

@Slf4j
@Component
public class RedisDistributedLock {

    /**
     * 锁等待时间,单位-秒
     */
    private static final int WAIT_TIME = 100;

    /**
     * 锁前缀
     */
    private static final String LOCKER_PREFIX = "redis-dis_lock:";

    /**
     * 锁工具类
     */
    private final RedissonClient redissonClient;
    private final NativeAsyncTaskExecutePool threadExecutePool;

    @Autowired
    public RedisDistributedLock(RedissonClient redissonClient, NativeAsyncTaskExecutePool threadExecutePool) {
        this.redissonClient = redissonClient;
        this.threadExecutePool = threadExecutePool;
    }

    /**
     * 尝试加锁, 最多等待 waitTime 定义的时间,默认100秒，锁定后经过lockTime秒后自动解锁
     *
     * @param <T>          参数类型，泛型指定返回类型
     * @param resourceName 资源名 key
     * @param callable     获取资源后的操作
     * @param waitTime     锁等待时间
     * @param lockTime     锁过期时间
     * @param fairLock     是否公平锁
     * @return the t
     */
    private <T> T lock(String resourceName, Callable<T> callable, int waitTime, int lockTime, boolean fairLock,
                       boolean isAsync) {

        RLock lock = null;
        try {
            lock = getLock(resourceName, fairLock);
            boolean success = lock.tryLock(waitTime, lockTime, TimeUnit.SECONDS);
            if (!success) {
                //获取锁失败,抛出异常
                throw new UtilsException(ExceptionEnum.SYS_REDIS_LOCK_RUNNING_ERROR);
            }

            //异步执行
            if (isAsync) {
                Objects.requireNonNull(this.threadExecutePool.getAsyncExecutor()).execute(() -> {
                    try {
                        callable.call();
                    } catch (Exception e) {
                        log.error("加锁异步运行失败", e);
                    }
                });
                return null;
            } else {
                return callable.call();
            }

        } catch (Exception e) {
            log.error("加锁运行失败", e);
            throw new UtilsException(ExceptionEnum.SYS_REDIS_LOCK_ERROR);
        } finally {
            if (null != lock && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private RLock getLock(String resourceName, boolean fairLock) {
        if (fairLock) {
            return redissonClient.getFairLock(generateKey(resourceName));
        }
        return redissonClient.getLock(generateKey(resourceName));
    }

    /**
     * 在锁里运行任务(同步执行)
     *
     * @param <T>          参数类型，泛型指定返回类型
     * @param resourceName 资源名 key
     * @param callable     获取资源后的操作
     * @param waitTime     锁等待时间
     * @param lockTime     锁过期时间
     * @param fairLock     是否公平锁
     * @return 处理完具体的业务逻辑要返回的数据 t
     */
    public <T> T runWithLockSync(String resourceName, Callable<T> callable, int waitTime, int lockTime,
                                 boolean fairLock) {
        return lock(resourceName, callable, waitTime, lockTime, fairLock, false);
    }

    /**
     * 在锁里运行任务(同步执行),默认使用公平锁
     *
     * @param <T>          参数类型，泛型指定返回类型
     * @param resourceName 资源名 key
     * @param callable     获取资源后的操作
     * @param waitTime     锁等待时间
     * @param lockTime     锁过期时间
     * @return 处理完具体的业务逻辑要返回的数据 t
     */
    public <T> T runWithLockSync(String resourceName, Callable<T> callable, int waitTime, int lockTime) {
        return lock(resourceName, callable, waitTime, lockTime, true, false);
    }

    /**
     * 在锁里运行任务(异步执行)
     *
     * @param resourceName 资源名 key
     * @param callable     获取资源后的操作
     * @param waitTime     锁等待时间
     * @param lockTime     锁过期时间
     * @param fairLock     是否公平锁
     */
    public <T> T runWithLockAsync(String resourceName, Callable<T> callable, int waitTime, int lockTime,
                                  boolean fairLock) {
        return lock(resourceName, callable, waitTime, lockTime, fairLock, true);

    }

    /**
     * 在锁里运行任务(异步执行),默认使用公平锁
     *
     * @param resourceName 资源名 key
     * @param callable     获取资源后的操作
     * @param waitTime     锁等待时间
     * @param lockTime     锁过期时间
     */
    public <T> T runWithLockAsync(String resourceName, Callable<T> callable, int waitTime, int lockTime) {
        return lock(resourceName, callable, waitTime, lockTime, true, true);

    }

    /**
     * 方法说明: 组合key
     *
     * @param key 标识
     * @return 返回组合key
     * @date 2020-08-11
     * @author lihao
     */
    private static String generateKey(String key) {
        return LOCKER_PREFIX + key;
    }

}
