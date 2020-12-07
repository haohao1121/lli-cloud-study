package com.sky.lli.controller;

import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.annotation.Action;
import com.baomidou.kisso.annotation.Login;
import com.baomidou.kisso.security.token.SSOToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/12/07
 */

@RestController
@RequestMapping("kis")
public class KiSsoController {

    @Login(action = Action.Skip)
    @ResponseBody
    @GetMapping("/test")
    public String home() {
        return "Hello Kisso!";
    }

    // 授权登录
    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        // 设置登录 COOKIE
        SSOHelper.setCookie(request, response, SSOToken.create().setIp(request).setId(1000).setIssuer("kisso"), false);
        return "login success!";
    }

    // 查看登录信息
    @GetMapping("/token")
    public String token(HttpServletRequest request) {
        String msg = "暂未登录";
        SSOToken ssoToken = SSOHelper.attrToken(request);
        if (null != ssoToken) {
            msg = "登录信息 ip=" + ssoToken.getIp();
            msg += "， id=" + ssoToken.getId();
            msg += "， issuer=" + ssoToken.getIssuer();
        }
        return msg;
    }

    // 退出登录
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        SSOHelper.clearLogin(request, response);
        return "Logout Kisso!";
    }

}
