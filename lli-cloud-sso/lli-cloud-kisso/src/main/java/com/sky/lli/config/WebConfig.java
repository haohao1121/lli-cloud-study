package com.sky.lli.config;

import com.baomidou.kisso.web.interceptor.SSOSpringInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/12/07
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // kisso 拦截器配置
        registry.addInterceptor(new SSOSpringInterceptor()).addPathPatterns("/*/**").excludePathPatterns("/kis/login");
    }

}
