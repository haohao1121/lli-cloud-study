package com.sky.lli.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

    public static final String PREFIX = "security-cas";

    /**
     * Security 过滤链定义，用于初始化默认的过滤规则 Map<pattern, Chain name>
     */
    private Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>(16);

    /**
     * cas filter不需要登录的路径
     */
    private List<String> ignorePattern = new ArrayList<>();

    /**
     * systemId,接入系统标识,后续使用枚举管理
     */
    private Integer systemId = 0;
}
