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
public class RoleInfo implements Serializable {
    private Integer id;
    private String roleName;
    private List<Permission> permissionList;
}
