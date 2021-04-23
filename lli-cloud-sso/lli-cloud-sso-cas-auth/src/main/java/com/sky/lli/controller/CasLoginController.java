package com.sky.lli.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述:
 *
 * @author lihao
 * @date 2021/4/19
 */

@Slf4j
@RestController
public class CasLoginController {


    /**
     * 1. cas 服务端会通过post请求，并且把用户信息以"用户名:密码"进行Base64编码放在authorization请求头中
     * 2. 返回200状态码并且格式为{"@class":"org.apereo.cas.authentication.principal.SimplePrincipal","id":"casuser","attributes":{}} 是成功的
     * 2. 返回状态码403用户不可用；404账号不存在；423账户被锁定；428过期；其他登录失败
     *
     * @param httpHeaders param
     */
    @PostMapping("/login")
    public Object login(@RequestHeader HttpHeaders httpHeaders) {

        log.info("开始验证服务");

        CasUser user = null;
        try {
            //获取用户参数
            UserTemp userTemp = obtainUserFormHeader(httpHeaders);
            //尝试查找用户库是否存在
            user = new CasUser();
            user.setUsername(userTemp.username);
            user.setPassword("21232f297a57a5a743894a0e4a801fc3");
            user.setState("0");

            //验证用户相关信息
            if (user != null) {
                if (!user.getPassword().equals(userTemp.password)) {
                    //密码不匹配
                    return new ResponseEntity(HttpStatus.BAD_REQUEST);
                }
                if (!"0".equals(user.getState())) {
                    //用户已锁定
                    return new ResponseEntity(HttpStatus.LOCKED);
                }
            } else {
                //不存在 404
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        } catch (UnsupportedEncodingException e) {
            log.error("用户认证错误", e);
            new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        //成功返回json
        return user;
    }

    /**
     * This allows the CAS server to reach to a remote REST endpoint via a POST for verification of credentials.
     * Credentials are passed via an Authorization header whose value is Basic XYZ where XYZ is a Base64 encoded version of the credentials.
     *
     * @param httpHeaders httpHeaders
     * @throws UnsupportedEncodingException ex
     */
    private UserTemp obtainUserFormHeader(HttpHeaders httpHeaders) throws UnsupportedEncodingException {

        //cas服务端会通过把用户信息放在请求头authorization中，并且通过Basic认证方式加密
        String authorization = httpHeaders.getFirst("authorization");
        if (StringUtils.isEmpty(authorization)) {
            return null;
        }

        String baseCredentials = authorization.split(" ")[1];
        //用户名:密码
        String usernamePassword = new String(Base64Utils.decodeFromString(baseCredentials), "UTF-8");
        String[] credentials = usernamePassword.split(":");

        return new UserTemp(credentials[0], credentials[1]);
    }

    /**
     * 从请求头中获取用户名和密码
     */
    private class UserTemp {
        private String username;
        private String password;

        public UserTemp(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }


    /**
     * cas-server rest login model
     */
    @Data
    public class CasUser {
        @JsonProperty("id")
        @NotNull
        private String username;

        /**
         * 需要返回实现org.apereo.cas.authentication.principal.Principal的类名接口
         */
        @JsonProperty("@class")
        private String clazz = "org.apereo.cas.authentication.principal.SimplePrincipal";


        @JsonProperty("attributes")
        private Map<String, Object> attributes = new HashMap<>();

        @JsonIgnore
        @NotNull
        private String password;

        /**
         * 用户状态,根据状态判断是否可用
         */
        @JsonIgnore
        private String state;
    }
}
