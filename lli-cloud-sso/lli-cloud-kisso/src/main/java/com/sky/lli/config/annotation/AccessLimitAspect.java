package com.sky.lli.config.annotation;

import cn.hutool.core.net.NetUtil;
import com.sky.lli.exception.ControllerException;
import com.sky.lli.exception.ExceptionEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 描述:
 *
 * @author lihao
 * @date 2021/2/1
 */

@Aspect
@Component
@ConditionalOnProperty(name = "lli.access-limit.enable", havingValue = "true")
public class AccessLimitAspect {

    private static final String LIMITING_KEY = "accessLimit:%s:%s";
    private static final String LIMITING_BEGINTIME = "beginTime";
    private static final String LIMITING_EXFREQUENCY = "exFrequency";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Pointcut(value = "@annotation(limiter)")
    public void pointcut(AccessLimit limiter) {
        //do something
    }

    @Around(value = "pointcut(limiter)", argNames = "pjp,limiter")
    public Object around(ProceedingJoinPoint pjp, AccessLimit limiter) throws Throwable {
        /*
         * 限定规则:
         *  1. ip + 方法名
         *     获取请求的ip,注意此处的ip一定要是初始访问者ip,nginx做多层代理,如果取不到原始ip会有并发问题
         *  2. 用户id + 方法名
         *     针对统一用户当前方法进行限定
         */

        //ip 地址
        String ipAddress = NetUtil.getLocalhostStr();
        String methodName = pjp.getSignature().toLongString();

        //获取方法的访问周期和频率
        long cycle = limiter.cycle();
        int frequency = limiter.count();
        long currentTime = System.currentTimeMillis();

        //获取redis中周期内第一次访问方法的时间和执行的次数
        Long beginTimeLong = (Long) redisTemplate.opsForHash().get(String.format(LIMITING_KEY, ipAddress, methodName), LIMITING_BEGINTIME);
        Integer exFrequencyLong = (Integer) redisTemplate.opsForHash().get(String.format(LIMITING_KEY, ipAddress, methodName), LIMITING_EXFREQUENCY);

        long beginTime = (beginTimeLong == null ? 0L : beginTimeLong);
        int exFrequency = (exFrequencyLong == null ? 0 : exFrequencyLong);

        //如果当前时间减去周期内第一次访问方法的时间大于周期时间，则正常访问
        //并将周期内第一次访问方法的时间和执行次数初始化
        if (currentTime - beginTime > cycle) {
            redisTemplate.opsForHash().put(String.format(LIMITING_KEY, ipAddress, methodName), LIMITING_BEGINTIME, currentTime);
            redisTemplate.opsForHash().put(String.format(LIMITING_KEY, ipAddress, methodName), LIMITING_EXFREQUENCY, 1);
            redisTemplate.expire(String.format(LIMITING_KEY, ipAddress, methodName), limiter.expireTime(), TimeUnit.SECONDS);
            return pjp.proceed();
        } else {
            //如果在周期时间内，执行次数小于频率，则正常访问
            //并将执行次数加一
            if (exFrequency < frequency) {
                redisTemplate.opsForHash().put(String.format(LIMITING_KEY, ipAddress, methodName), LIMITING_EXFREQUENCY, exFrequency + 1);
                redisTemplate.expire(String.format(LIMITING_KEY, ipAddress, methodName), limiter.expireTime(), TimeUnit.SECONDS);
                return pjp.proceed();
            } else {
                //否则抛出访问频繁异常
                throw new ControllerException(ExceptionEnum.SYS_REQUEST_TOO_FREQUENT);
            }
        }
    }

}