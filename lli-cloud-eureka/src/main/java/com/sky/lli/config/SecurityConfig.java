package com.sky.lli.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 说明:
 *
 * @author klaus
 * @date 2020/8/16
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Spring Security在高版本默认开启了csrf跨域请求伪造由,
        // 于等会要创建的Eureka Client与Eureka Server端口不同产生了跨域问题，这里我们需要写一个配置类关闭csrf
        http.csrf().disable();
        super.configure(http);
    }
}
