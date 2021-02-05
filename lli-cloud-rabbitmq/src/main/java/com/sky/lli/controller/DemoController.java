package com.sky.lli.controller;

import com.sky.lli.model.DemoModel;
import com.sky.lli.service.DemoService;
import com.sky.lli.util.restful.ResponseResult;
import com.sky.lli.util.restful.ResultResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：
 * CLASSPATH: com.sky.lli.controller.DemoController
 * VERSION:   1.0
 * DATE: 2019-04-24
 *
 * @author lihao
 */

@RestController
@RequestMapping("/demo")
public class DemoController {

    private static final String TEST_SEND_MESSAGE = "testSendMsg";


    private final DemoService demoService;

    @Autowired
    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping(TEST_SEND_MESSAGE)
    public ResponseResult<Object> testSendMsg() {
        DemoModel obj = DemoModel.builder().age(11).id("111").name("sdfasfa").build();
        this.demoService.testSend(obj);

        return ResultResponseUtils.success();
    }

}
