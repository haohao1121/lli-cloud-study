package com.sky.lli.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 说明:
 *
 * @author klaus
 * @date 2021/4/25
 */

@Getter
@Setter
@Component
@ConfigurationProperties(CasProperties.PREFIX)
public class CasProperties {

    public static final String PREFIX = "cas-authc";

    /**
     * cas filter不需要登录的路径
     */
    private List<String> ignorePattern;
}
