package com.sky.lli.controller;

import com.sky.lli.util.restful.ResponseResult;
import com.sky.lli.util.restful.ResultResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：健康检查
 * CLASSPATH: com.sky.lli.controller.HealthController
 *
 * @author klaus
 * @date 2019-05-23
 */

@Slf4j
@RestController
public class HealthController {

    /**
     * 状态检查
     */
    private static final String HEALTH_CHECK = "healthCheck";

    /**
     * @date 2020/8/10
     * @author klaus
     * 方法说明: 健康检查接口
     */
    @GetMapping(HEALTH_CHECK)
    public ResponseResult<Object> healthCheck() {
        log.info("服务健康检查");
        return ResultResponseUtils.success("服务正常,谢谢使用!");
    }
}
