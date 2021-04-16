package com.sky.lli.controller;

import com.sky.lli.util.restful.ResponseResult;
import com.sky.lli.util.restful.ResultResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述:
 *
 * @author lihao
 * @date 2021/4/16
 */

@Slf4j
@RestController
public class TestController {

    /**
     * 方法说明: 测试方法
     *
     * @return 测试数据
     */
    @GetMapping("client1")
    public ResponseResult<Object> client() {
        log.info("client1");

        return ResultResponseUtils.success("client-1");
    }
}
