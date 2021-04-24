package com.sky.lli;

import lombok.Data;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 说明:
 *
 * @author klaus
 * @date 2021/4/25
 */
@Data
@ConfigurationProperties(ShiroProperties.PREFIX)
public class ShiroProperties {
    public static final String PREFIX = "shiro";

    /**
     * The login url to used to authenticate a user, used when redirecting users if authentication is required.
     */
    private String loginUrl = AccessControlFilter.DEFAULT_LOGIN_URL;
    /**
     * Redirect address after successful login
     */
    private String successUrl = AuthenticationFilter.DEFAULT_SUCCESS_URL;
    /**
     * The URL to where the user will be redirected after logout.
     */
    private String redirectUrl = LogoutFilter.DEFAULT_REDIRECT_URL;
    /**
     * Failure Url: Jump path when authentication fails
     */
    private String failureUrl;
    /**
     * The URL to which users should be redirected if they are denied access to an underlying path or resource,
     * (401 Unauthorized).
     */
    private String unauthorizedUrl;

    /**
     * shiro filter
     */
    private Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
}
