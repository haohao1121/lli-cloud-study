package com.sky.lli.config;

import org.jasig.cas.client.boot.configuration.CasClientConfigurer;
import org.jasig.cas.client.configuration.ConfigurationKeys;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 *
 * @author lihao
 * @date 2021/4/22
 */
@Configuration
public class MyCasClientConfiguration implements CasClientConfigurer {
    /**
     * Configure or customize CAS authentication filter.
     *
     * @param authenticationFilter the authentication filter
     */
    @Override
    public void configureAuthenticationFilter(FilterRegistrationBean authenticationFilter) {

        //不需要认证的路径
        List<String> ignoreUrlPattern = new ArrayList<>();
        ignoreUrlPattern.add("/healthCheck");
        ignoreUrlPattern.add("/test/*");

        authenticationFilter.addInitParameter(ConfigurationKeys.IGNORE_PATTERN.getName(), String.join("|", ignoreUrlPattern));
        authenticationFilter.addInitParameter(ConfigurationKeys.IGNORE_URL_PATTERN_TYPE.getName(), ConfigurationKeys.IGNORE_URL_PATTERN_TYPE.getDefaultValue());
        authenticationFilter.setOrder(1);
    }
}
