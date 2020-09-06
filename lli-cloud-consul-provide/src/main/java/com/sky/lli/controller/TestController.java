package com.sky.lli.controller;

import com.sky.lli.config.TestConsulConfig;
import com.sky.lli.util.restful.ResponseResult;
import com.sky.lli.util.restful.ResultResponseUtils;
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
    private TestConsulConfig testConsulConfig;

    @GetMapping("/provide")
    public ResponseResult<Object> provide() {
        log.info(" 收到请求");
        log.info(" testConfig:{}", testConsulConfig.getName());

        return ResultResponseUtils.success(testConsulConfig.getName());
    }
}
