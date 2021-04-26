package com.sky.lli.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 用于加载用户信息
 * 实现UserDetailsService接口，或者实现AuthenticationUserDetailsService接口
 *
 * @author kluas
 */

@Slf4j
@Component
public class CasUserDetailsServiceImpl implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

    @Override
    public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) {
        String username = token.getName();
        log.debug("current username [{}]", username);
        // 这里应该查询数据库获取具体的用户信息和权限信息
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        /*
         * SimpleGrantedAuthority
         * 注意:
         *      1.以 ROLE_  开头,代表 角色名称,hasAnyRole方法验证的时候 默认会拼接 "ROLE_" 与当前值匹配
         *      2.否则代表权限
         */
        authorities.add(new SimpleGrantedAuthority("ROLE_BASE_ROLE"));
        authorities.add(new SimpleGrantedAuthority("admin"));
        return new User(username, username, authorities);
    }
}
