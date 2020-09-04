package com.sky.lli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/09/04
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ConsulConsumeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsulConsumeApplication.class, args);
    }
}
