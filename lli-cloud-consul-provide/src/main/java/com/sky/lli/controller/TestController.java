package com.sky.lli.controller;

import com.sky.lli.util.restful.ResponseResult;
import com.sky.lli.util.restful.ResultResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/09/04
 */

@Slf4j
@RestController
public class TestController {

    @GetMapping("/provide")
    ResponseResult<Object> provide() {
        log.info(" 收到请求");
        return ResultResponseUtils.success();
    }
}
