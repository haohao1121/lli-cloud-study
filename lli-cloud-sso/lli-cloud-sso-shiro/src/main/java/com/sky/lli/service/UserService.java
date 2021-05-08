package com.sky.lli.service;

import com.sky.lli.model.UserInfo;

/**
 * 描述:
 *
 * @author lihao
 * @date 2021/5/8
 */
public interface UserService {

    /**
     * @date 2021/5/8
     * @author lihao
     * 方法说明: 根据用户名获取用户信息
     */
    UserInfo findUserByAccout(String userAccout);
}
