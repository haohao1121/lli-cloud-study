package com.sky.lli.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 说明: 文件索引信息
 *
 * @author klaus
 * @date 2020/8/22
 */

@Data
@Document(collation = "FILE_INDEX")
public class FileIndex {

    @Id
    @ApiModelProperty(value = "文件唯一号")
    private String uniqNo;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件类型")
    private String fileType;

    @ApiModelProperty(value = "文件路径")
    private String filePath;

    @ApiModelProperty(value = "文件完整路径")
    private String fullPath;

    @ApiModelProperty(value = "文件下载地址(Nginx服务地址)")
    private String downloadUrl;

    @ApiModelProperty(value = "上传时间")
    private String createTime;

    @ApiModelProperty(value = "文件来源(来自Cloud服务中哪个项目)")
    private String fileSource;
}
