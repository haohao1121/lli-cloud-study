package com.sky.lli.config.handlerinterceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Feign 请求拦截器
 *
 * @author klaus
 * @date 2020-01-01
 */
@Slf4j
@Configuration
public class FeignTokenInterceptor implements RequestInterceptor {
    private static final String TOKEN = "Token";
    private static final String MDC_TAG = "MdcTag";

    @Override
    public void apply(RequestTemplate template) {
        // 上下文获取 Token 并追加到 Feign 请求上
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader(TOKEN);
        template.header(TOKEN, token);
        //MDC请求链路唯一标识
        template.header(MDC_TAG, MDC.get(MDC_TAG));
        // log 记录本次请求信息
        log.info("Feign 请求地址:{},携带 Token:{},MdcTag:{}", template.request().url(), token, MDC.get(MDC_TAG));
    }
}