package com.sky.lli.controller;

import com.sky.lli.remote.TestProvideService;
import com.sky.lli.util.restful.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/08/19
 */

@Slf4j
@RestController
public class TestController {

    @Resource
    private TestProvideService testProvideService;

    /**
     * 方法说明: 测试方法
     *
     * @return 测试数据
     */
    @GetMapping("consume")
    public ResponseResult<Object> consume() {
        log.info("消费者发起的请求");

        return testProvideService.provide("testFeinClient");
    }
}
