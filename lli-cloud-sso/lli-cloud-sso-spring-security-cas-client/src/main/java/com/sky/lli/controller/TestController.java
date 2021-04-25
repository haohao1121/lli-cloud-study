package com.sky.lli.controller;

import com.sky.lli.exception.ControllerException;
import com.sky.lli.exception.ExceptionEnum;
import com.sky.lli.util.restful.ResponseResult;
import com.sky.lli.util.restful.ResultResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AssertionHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URLEncoder;

/**
 * 描述:
 *
 * @author lihao
 * @date 2021/4/16
 */

@Slf4j
@RestController
public class TestController {

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("hello")
    public ResponseResult<Object> hello() {
        log.info("hello收到请求");
        return ResultResponseUtils.success("hello");
    }

    /**
     * 方法说明: 测试方法
     *
     * @return 测试数据
     */
    @GetMapping("client")
    public ResponseResult<Object> client() {
        String baseUrl = "http://localhost:7502/hello";
        URI url = getProxyUrl(baseUrl);
        String str = restTemplate.getForObject(url, String.class);
        return ResultResponseUtils.success(str);
    }

    private URI getProxyUrl(String baseUrl) {
        try {
            //1、获取到AttributePrincipal对象
            AttributePrincipal principal = AssertionHolder.getAssertion().getPrincipal();
            //2、获取对应的(PT)proxy ticket
            String proxyTicket = principal.getProxyTicketFor(baseUrl);
            //3、请求被代理应用时将获取到的proxy ticket以参数ticket进行传递
            return new URI(baseUrl + "?ticket=" + URLEncoder.encode(proxyTicket, "UTF-8"));
        } catch (Exception e) {
            throw new ControllerException(ExceptionEnum.SYS_FAILURE_EXCEPTION);
        }
    }

    @GetMapping("test/ccc")
    public ResponseResult<Object> ccc() {
        log.info("ccc收到请求");
        //不需要登录
        return ResultResponseUtils.success("当前方法不需要认证");
    }
}
