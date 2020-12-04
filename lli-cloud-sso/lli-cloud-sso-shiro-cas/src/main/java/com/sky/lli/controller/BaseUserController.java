package com.sky.lli.controller;

import com.sky.lli.dao.entity.BaseUser;
import com.sky.lli.service.BaseUserService;
import com.sky.lli.util.restful.ResponseResult;
import com.sky.lli.util.restful.ResultResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/12/04
 */

@Slf4j
@RestController
@RequestMapping("user")
public class BaseUserController {

    private final BaseUserService baseUserService;

    @Autowired
    public BaseUserController(BaseUserService baseUserService) {this.baseUserService = baseUserService;}

    /**
     * 方法说明: 添加用户
     *
     * @param user user
     *
     * @date 2020-12-04
     * @author lihao
     */
    @PostMapping("addUser")
    public ResponseResult<Object> addUser(@RequestBody BaseUser user) {

        boolean save = this.baseUserService.save(user);
        log.info("save user result:{}", save);
        BaseUser baseUser = this.baseUserService.getById(user.getId());
        baseUser.setUserName("qqq");
        baseUser.setLockVersion(4);
        boolean update = this.baseUserService.updateById(baseUser);
        log.info("update by id result:{}", update);

        return ResultResponseUtils.success(user.getId());
    }
}
