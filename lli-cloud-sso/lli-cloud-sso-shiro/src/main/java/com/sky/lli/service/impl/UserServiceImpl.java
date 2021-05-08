package com.sky.lli.service.impl;

import com.sky.lli.model.Permission;
import com.sky.lli.model.RoleInfo;
import com.sky.lli.model.UserInfo;
import com.sky.lli.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 *
 * @author lihao
 * @date 2021/5/8
 */
@Service
public class UserServiceImpl implements UserService {
    /**
     * @param userAccout name
     * @date 2021/5/8
     * @author lihao
     * 方法说明: 根据用户名获取用户信息
     */
    @Override
    public UserInfo findUserByAccout(String userAccout) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserAccout(userAccout);
        userInfo.setUserName(userAccout);
        userInfo.setUserPwd(userAccout);
        RoleInfo roleAdmin = new RoleInfo();
        roleAdmin.setRoleName("admin");
        RoleInfo roleVip = new RoleInfo();
        roleVip.setRoleName("vip");
        RoleInfo roleGuest = new RoleInfo();
        roleGuest.setRoleName("guest");

        List<RoleInfo> roleInfoList = new ArrayList<>();
        roleInfoList.add(roleGuest);
        roleInfoList.add(roleVip);
        roleInfoList.add(roleAdmin);

        userInfo.setRoleList(roleInfoList);


        Permission p1 = new Permission();
        p1.setPermissionName("add");
        Permission p2 = new Permission();
        p2.setPermissionName("update");
        Permission p3 = new Permission();
        p3.setPermissionName("test");
        Permission p4 = new Permission();
        p4.setPermissionName("123");


        List<Permission> permissionList = new ArrayList<>();
        permissionList.add(p4);
        permissionList.add(p1);
        permissionList.add(p2);
        permissionList.add(p3);
        roleAdmin.setPermissionList(permissionList);


        return userInfo;
    }
}
