package com.sky.lli.config.annotation;

import com.sky.lli.utils.lock.RedisDistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/11/27
 */

@Aspect
@Component
public class DistributedLockAspect {

    private RedisDistributedLock lockTemplate;

    @Autowired
    public DistributedLockAspect(RedisDistributedLock lockTemplate) {
        this.lockTemplate = lockTemplate;
    }

    @Pointcut("@annotation(com.sky.lli.config.annotation.DistributedLock)")
    public void distributedLockAspect() {}

    @Around(value = "distributedLockAspect()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

        //切点所在的类
        Class targetClass = pjp.getTarget().getClass();
        //使用了注解的方法
        String methodName = pjp.getSignature().getName();

        Class[] parameterTypes = ((MethodSignature) pjp.getSignature()).getMethod().getParameterTypes();

        Method method = targetClass.getMethod(methodName, parameterTypes);

        Object[] arguments = pjp.getArgs();

        final String lockName = getLockName(method, arguments);

        return lock(pjp, method, lockName);
    }

    @AfterThrowing(value = "distributedLockAspect()", throwing = "ex")
    public void afterThrowing(Throwable ex) {
        throw new RuntimeException(ex);
    }

    private String getLockName(Method method, Object[] args) {
        Objects.requireNonNull(method);
        DistributedLock annotation = method.getAnnotation(DistributedLock.class);

        String lockName = annotation.lockName();

        if (isEmpty(lockName)) {
            lockName = method.getName();
        }
        return lockName;
    }

    private Object lock(ProceedingJoinPoint pjp, Method method, final String lockName) {

        DistributedLock annotation = method.getAnnotation(DistributedLock.class);

        //是否公平锁
        boolean fairLock = annotation.fairLock();
        //是否异步
        boolean asyncLock = annotation.asyncLock();
        //等待时间和过期释放时间
        int waitTime = annotation.waitTime(), leaseTime = annotation.leaseTime();

        if (asyncLock) {
            return lockTemplate.runWithLockAsync(lockName, () -> proceed(pjp), waitTime, leaseTime, fairLock);
        } else {
            return lockTemplate.runWithLockSync(lockName, () -> proceed(pjp), waitTime, leaseTime, fairLock);
        }
    }

    private Object proceed(ProceedingJoinPoint pjp) {
        try {
            return pjp.proceed();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    private boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }

}
