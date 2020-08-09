package com.sky.lli.config.handlerfilter;


import ch.qos.logback.classic.ClassicConstants;
import cn.hutool.core.net.NetUtil;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

import static java.util.Objects.isNull;


/**
 * 描述：配置MDC
 *
 * @author klaus
 * @date 2020-01-01
 */
@Configuration
public class MDCFilterConfigurer {
    @Bean
    @ConditionalOnProperty(name = "mdc.enable", havingValue = "true")
    public FilterRegistrationBean mdcFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CustomMDCInsertingServletFilter());
        registration.addUrlPatterns("/*");
        registration.setName("mdcFilter");
        registration.setOrder(1);
        return registration;
    }


    /**
     * 描述：MDC
     *
     * @author klaus
     * @date 2020-01-01
     */
    public class CustomMDCInsertingServletFilter implements Filter {

        //请求Token
        private static final String TOKEN = "Token";

        @Override
        public void destroy() {
            // Do nothing because of nothing need to do.
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            insertIntoMDC(request);
            try {
                chain.doFilter(request, response);
            } finally {
                MDC.clear();
            }
        }

        void insertIntoMDC(ServletRequest request) {
            MDC.put(MDCLogsNames.REQUEST_UUID.getMdczh(), UUID.randomUUID().toString());
            MDC.put(MDCLogsNames.REQUEST_HOST_NAME.getMdczh(), request.getRemoteHost());
            if (request instanceof HttpServletRequest) {
                HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                if (CollectionUtils.isEmpty(httpServletRequest.getParameterMap())) {
                    MDC.put(MDCLogsNames.REQUEST_PARAM.getMdczh(), httpServletRequest.getParameterMap().toString());
                }
                if (!isNull(httpServletRequest.getHeader(TOKEN))) {
                    MDC.put(MDCLogsNames.REQUEST_TOKEN.getMdczh(), httpServletRequest.getHeader(TOKEN));
                }
                MDC.put(MDCLogsNames.REQUEST_URI.getMdczh(), httpServletRequest.getRequestURI());
                StringBuffer requestURL = httpServletRequest.getRequestURL();
                if (requestURL != null) {
                    MDC.put(MDCLogsNames.REQUEST_URL.getMdczh(), requestURL.toString());
                }
                MDC.put(MDCLogsNames.REQUEST_METHOD.getMdczh(), httpServletRequest.getMethod());
                MDC.put(MDCLogsNames.REQUEST_CLIENT_IP.getMdczh(), NetUtil.getLocalhostStr());
                MDC.put(ClassicConstants.REQUEST_USER_AGENT_MDC_KEY, httpServletRequest.getHeader("User-Agent"));
                MDC.put(ClassicConstants.REQUEST_X_FORWARDED_FOR, httpServletRequest.getHeader("X-Forwarded-For"));
            }
        }

        @Override
        public void init(FilterConfig arg0) {
            // Do nothing because of nothing need to do.
        }
    }

}