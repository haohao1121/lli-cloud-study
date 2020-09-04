package com.sky.lli.controller;

import com.sky.lli.remote.TestFeignService;
import com.sky.lli.util.restful.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/09/04
 */

@Slf4j
@RestController
public class TestController {

    @Resource
    private TestFeignService testFeignService;

    @GetMapping("/consume")
    ResponseResult<Object> consume() {
        log.info(" 发起请求");
        return this.testFeignService.provide();
    }
}