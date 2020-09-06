package com.sky.lli.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 说明:
 *
 * @author klaus
 * @date 2020/9/6
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "lli")
public class TestConsulConfig {

    private String name;
}
