package com.sky.lli.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 描述:
 *
 * @author lihao
 * @date 2021/5/8
 */
@Data
public class Permission implements Serializable {
    private Integer id;
    private String permissionName;
}
