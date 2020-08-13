package com.sky.lli.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author klaus
 * @date 2020/8/14
 * 类名：Oss
 * 描述：阿里OSS配置
 */

@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssProperties {
    /**
     * 阿里OSS服务地址
     */
    private String endpoint;

    /**
     * 访问ID
     */
    private String accessKeyId;

    /**
     * 访问密码
     */
    private String accessKeySecret;

    /**
     * 存放空间
     */
    private String bucketName;

    /**
     * 公网访问空间
     */
    private String comBucketName;

    /**
     * 公网访问地址前缀
     */
    private String publicUrlPrefix;
}
