package com.sky.lli.controller;

import com.sky.lli.distributed.lock.zookeeper.ZookeeperDistributedLock;
import com.sky.lli.utils.lock.ZkDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Random;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/11/25
 */

@Slf4j
@RestController
@RequestMapping("/zk-lock")
public class ZookeeperLockTestController {

    @Resource
    private ZookeeperDistributedLock zookeeperDistributedLock;

    @Resource
    private ZkDistributedLock zkDistributedLock;

    @ResponseBody
    @GetMapping("/test")
    public String test() {

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {

                String lockPath = "testlock";
                zkDistributedLock.runWithLockSync(lockPath, 10, () -> {
                    doTask();
                    return null;
                });

            }).start();
        }

        return "zk-lock";
    }

    private void doTask() {
        System.out.println(Thread.currentThread().getName() + " 抢到锁!");
        Random random = new Random();
        int _int = random.nextInt(200);
        System.out.println(Thread.currentThread().getName() + " sleep " + _int + "millis");
        try {
            Thread.sleep(_int);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " 释放锁!");
    }

}
