package com.sky.lli.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 描述：文档模型的索引信息存在一一描述关系
 *
 * @author klaus
 * @date 2020/8/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "FILE_INDEX")
@ApiModel("文件索引信息")
public class FileIndex implements Serializable {

    /**
     * 文件唯一号
     */
    @Id
    @ApiModelProperty("文件唯一号")
    private String fileUniqueId;

    /**
     * 文件名称
     */
    @ApiModelProperty("文件名称")
    private String fileName;

    /**
     * 上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("上传时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("修改时间")
    private LocalDateTime updateTime;

    /**
     * 需要保存的附加参数
     */
    @ApiModelProperty("需要保存的附加参数")
    private Map<String, String> attachMap;

    /**
     * 文件类型
     */
    @ApiModelProperty("文件类型")
    private String fileType;

    /**
     * 文件描述
     */
    @ApiModelProperty("文件描述")
    private String fileDesc;

    /**
     * 是否有效1表示有效0表示无效
     */
    @ApiModelProperty("是否有效: 1-表示有效 0-表示无效")
    private Integer effect;

    /**
     * 在ftp上保存的路径
     */
    @ApiModelProperty("文件存储路径")
    private String filePath;

    /**
     * 文件是否存在公有域(公网访问)
     */
    @ApiModelProperty("文件是否存在公有域(公网访问)")
    private Boolean isPrivate;

}
