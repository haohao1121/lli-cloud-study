package com.sky.lli.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述：
 * CLASSPATH: com.sky.lli.model.TemplateParam
 * VERSION:   1.0
 * DATE: 2019-12-13
 *
 * @author lihao
 */

@Data
@ApiModel(value = "静态资源图片参数")
public class TemplateParam implements Serializable {

    @ApiModelProperty(value = "属性名称")
    private String propertyName;
    @ApiModelProperty(value = "属性值")
    private String propertyValue;
    @ApiModelProperty(value = "属性类型,0:普通文本 1:文件地址")
    private Integer propertyType = 0;
}
