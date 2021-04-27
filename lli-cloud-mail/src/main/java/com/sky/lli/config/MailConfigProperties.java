package com.sky.lli.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 说明:
 *
 * @author klaus
 * @date 2021/4/25
 */

@Data
@Component
@ConfigurationProperties(MailConfigProperties.PREFIX)
public class MailConfigProperties {

    public static final String PREFIX = "lli-mail";

    /**
     * 临时文件下载地址
     */
    private String tempFileDownloadPath;
}
