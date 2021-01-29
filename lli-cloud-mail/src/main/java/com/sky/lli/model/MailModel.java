package com.sky.lli.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 描述：
 * CLASSPATH: com.sky.lli.model.MailModel
 * VERSION:   1.0
 * DATE: 2019-05-22
 *
 * @author lihao
 */

@Data
@ApiModel(value = "邮件参数")
public class MailModel implements Serializable {

    @ApiModelProperty(value = "主键id")
    private String id;
    @ApiModelProperty(value = "邮件发送人,默认当前邮箱账号")
    private String from;
    @ApiModelProperty(value = "邮件接收人（多个邮箱则用逗号[,]隔开）")
    private String to;
    @ApiModelProperty(value = "邮件主题")
    private String subject;
    @ApiModelProperty(value = "邮件内容")
    private String text;
    @ApiModelProperty(value = "抄送（多个邮箱则用逗号[,]]隔开）")
    private String cc;
    @ApiModelProperty(value = "密送（多个邮箱则用逗号[,]]隔开）")
    private String bcc;

    @JsonIgnore
    @ApiModelProperty(value = "邮件附件")
    private MultipartFile[] multipartFiles;
    @ApiModelProperty(value = "邮件附件链接地址")
    private Map<String, String> attachmentMap;

    @ApiModelProperty(value = "邮件模板名称")
    private String templateName;
    @ApiModelProperty(value = "邮件模板参数")
    private List<TemplateParam> templateParams;

    @ApiModelProperty(value = "静态资源图片参数")
    private Map<String, String> mailInLineMap;
}
