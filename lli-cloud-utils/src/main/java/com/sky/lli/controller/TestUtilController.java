package com.sky.lli.controller;

import com.sky.lli.utils.lock.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/11/25
 */

@Slf4j
@RestController
@RequestMapping("/util")
public class TestUtilController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisLock redisLock;

    @ResponseBody
    @GetMapping("/test")
    public String testRedisSentinel(String key, String value) {
        this.stringRedisTemplate.opsForValue().set(key, value);
        log.info("redis-sentinel放入key:[{}]", key);

        this.redisLock.runWithLockSync(key, value, TimeUnit.MINUTES, 10, () -> {
            testLock();
            return null;
        });
        return this.stringRedisTemplate.opsForValue().get(key);
    }

    private void testLock() {
        log.info("redis-lock执行");
    }

}
