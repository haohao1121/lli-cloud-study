package com.sky.lli.controller;

import com.sky.lli.util.ContextHolder;
import io.buji.pac4j.subject.Pac4jPrincipal;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 描述:
 *
 * @author lihao
 * @date 2021/4/25
 */
@RestController
public class HelloController {

    @Resource
    private RestTemplate restTemplate;

    @RequestMapping("hello")
    public String sayHi() {
        Pac4jPrincipal p = ContextHolder.getPac4jPrincipal();
        return "hello now:" + System.currentTimeMillis() + "  name=" + p.getName();
    }

    @RequestMapping("/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "login";
    }

    @GetMapping("client")
    public String client() {
        String url = "http://localhost:8402/client";
        String str = restTemplate.getForObject(url, String.class);
        return str;
    }

    @GetMapping("test/aaa")
    public Object test() {
        //都不需要登录
        String url = "http://localhost:8402/test";
        String str = restTemplate.getForObject(url, String.class);
        return str;
    }

    @GetMapping("test/bbb")
    public Object a2b() {
        //不需要登录条用需要登录
        String url = "http://localhost:8402/client";
        String str = restTemplate.getForObject(url, String.class);
        return str;
    }
}
