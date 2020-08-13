package com.sky.lli.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author klaus
 * @date 2020/8/14
 * 类名：FtpProperties
 * 描述：ftp配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "ftp")
public class FtpProperties {
    /**
     * ftp端口
     */
    private int port;
    /**
     * ftp主机ip
     */
    private String host;
    /**
     * ftp用户名
     */
    private String userName;
    /**
     * ftp密码
     */
    private String userPassword;
}
