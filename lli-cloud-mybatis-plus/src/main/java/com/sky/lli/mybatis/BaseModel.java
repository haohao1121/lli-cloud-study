package com.sky.lli.mybatis;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author code-generator
 * @date 2020/06/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class BaseModel<T extends Model<T>> extends Model<T> {

    private static final long serialVersionUID = 840330757657609176L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    protected Long id;

    /**
     * Version 注解说明：更新时，实体对象的version属性必须有值，才会生成SQL update ... WHERE ... and version=?
     */
    @Version
    @TableField(value = "lock_version", fill = FieldFill.INSERT, update = "%s+1")
    protected Integer lockVersion;

}