package com.sky.lli.config.handlerinterceptor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置 Feign
 *
 * @author klaus
 * @date 2020-01-01
 */
@Configuration
@ConditionalOnProperty(name = "lli.feign.token.enable", havingValue = "true", matchIfMissing = true)
public class FeignConfiguration {
    /**
     * 添加设置认证 token
     */
    @Bean
    public FeignTokenInterceptor basicAuthRequestInterceptor() {
        return new FeignTokenInterceptor();
    }
}