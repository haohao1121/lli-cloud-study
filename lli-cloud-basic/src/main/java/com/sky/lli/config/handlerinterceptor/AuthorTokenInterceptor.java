package com.sky.lli.config.handlerinterceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 描述:
 *
 * @author klaus
 * @date 2020/8/9
 */

@Slf4j
@Configuration
@ConditionalOnProperty(name = "lli.feign.auto.token", havingValue = "true")
public class AuthorTokenInterceptor implements WebMvcConfigurer {
    private static final String TOKEN = "Token";

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor());
    }

    @Bean
    public HandlerInterceptor tokenInterceptor() {
        return new HandlerInterceptorAdapter() {
            /**
             * 获取 Token
             *
             * @param request  请求
             * @param response 响应
             * @param handler  handler
             *
             * @return boolean 过不过
             */
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                //请求Token
                String token = request.getHeader(TOKEN);
                RequestTokenContext.getCurrentContext().set(TOKEN, token);
                log.debug("请求 Token:{}", token);
                return true;
            }

            /**
             * 处理结束删除 current
             *
             * @param request  请求
             * @param response 响应
             * @param handler  handler
             * @param ex       ex
             */
            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
                RequestTokenContext.unset();
            }
        };
    }
}