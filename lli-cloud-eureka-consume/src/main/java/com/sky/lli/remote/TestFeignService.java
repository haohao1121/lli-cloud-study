package com.sky.lli.remote;

import com.sky.lli.util.restful.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/08/19
 */

@Component
@FeignClient(value = "LLI-CLOUD-EUREKA-PROVIDE")
public interface TestFeignService {

    /**
     * 方法说明: 测试FeignClient
     *
     * @param name 参数
     *
     * @return 数据
     */
    @GetMapping("provide")
    @ResponseBody
    ResponseResult<Object> provide(@RequestParam("requstName") String name);
}
