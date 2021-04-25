package com.sky.lli.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 说明:
 *
 * @author klaus
 * @date 2021/4/25
 */

@Data
@Component
@ConfigurationProperties(SecurityCasProperties.PREFIX)
public class SecurityCasProperties {

    public static final String PREFIX = "spring.security";

    /**
     * Security 过滤链定义，用于初始化默认的过滤规则 Map<pattern, Chain name>
     */
    private Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>(16);
}
