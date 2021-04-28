package com.sky.lli.dao.entity;

import com.sky.lli.mybatis.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author lli
 * @since 2020-12-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "BaseUser对象", description = "BaseUser对象")
public class BaseUser extends BaseModel<BaseUser> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "登录名")
    private String loginName;

    @ApiModelProperty(value = "密码")
    private String loginPwd;

    @ApiModelProperty(value = "手机号")
    private String telephone;

    @Override
    protected Serializable pkVal() {
        return null;
    }

}
