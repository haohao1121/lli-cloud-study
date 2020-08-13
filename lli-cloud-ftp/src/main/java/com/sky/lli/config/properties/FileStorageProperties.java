package com.sky.lli.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 方法说明: 文件存储配置
 *
 * @author klaus
 * @date 2020/8/14
 */
@Component
@ConfigurationProperties(prefix = "storage")
@Data
public class FileStorageProperties {

    /**
     * 文件下载目录
     */
    private String downloadPath;

    /**
     * 文件上传目录
     */
    private String uploadPath;

    /**
     * 文件临时目录
     */
    private String tempPath;

    /**
     * 视频编码转码
     */
    private String videoPath;

    /**
     * 预览路径
     */
    private String previewPath;
}
