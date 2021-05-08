package com.sky.lli.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述:
 *
 * @author lihao
 * @date 2021/5/8
 */
@Data
public class UserInfo implements Serializable {

    private Integer userId;
    private String userName;
    private String userAccout;
    private String userPwd;
    private List<RoleInfo> roleList;
}
