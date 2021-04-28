package com.sky.lli.mybatis;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author code-generator
 * @date 2020/06/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class BaseEntity<T extends BaseModel<T>> extends BaseModel<T> {

    private static final long serialVersionUID = 840330757657609176L;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    protected LocalDateTime createTime;

    @ApiModelProperty(value = "创建人")
    protected Integer createUser;

    @TableField(value = "update_time", fill = FieldFill.INSERT, update = "NOW()")
    protected LocalDateTime updateTime;

    @ApiModelProperty(value = "更新人")
    protected Integer updateUser;

}