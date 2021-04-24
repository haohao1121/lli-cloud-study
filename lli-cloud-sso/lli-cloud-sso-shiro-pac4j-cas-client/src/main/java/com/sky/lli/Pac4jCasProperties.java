package com.sky.lli;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 说明:
 *
 * @author klaus
 * @date 2021/4/25
 */
@Data
@ConfigurationProperties(Pac4jCasProperties.PREFIX)
public class Pac4jCasProperties {
    public static final String PREFIX = "shiro.cas";

    /**
     * CAS server URL E.g. https://example.com/cas or https://cas.example. Required.
     */
    private String serverUrlPrefix;

    /**
     * CAS server login URL E.g. https://example.com/cas/login or https://cas.example/login. Required.
     */
    private String serverLoginUrl;

    /**
     * CAS server logout URL E.g. https://example.com/cas/logout or https://cas.example/logout. Required.
     */
    private String serverLogoutUrl;

    /**
     * CAS-protected client application host URL E.g. https://myclient.example.com Required.
     */
    private String clientHostUrl;
    /**
     * CAS-protected client application name URL E.g. lli-app Required.
     */
    private String clientName;

}
