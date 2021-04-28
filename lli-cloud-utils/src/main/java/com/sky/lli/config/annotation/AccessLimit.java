package com.sky.lli.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述:
 *
 * @author lihao
 * @date 2021/2/1
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AccessLimit {

    /**
     * 从第一次访问接口的时间到cycle周期时间内最大访问次数
     */
    int count() default 20;

    /**
     * 周期时间,单位ms：
     * 默认周期时间为一分钟
     */
    long cycle() default 60 * 1000;

    /**
     * 到期时间,单位s：
     * 如果在cycle周期时间内超过frequency次，则默认1分钟内无法继续访问
     */
    long expireTime() default 60;
}
