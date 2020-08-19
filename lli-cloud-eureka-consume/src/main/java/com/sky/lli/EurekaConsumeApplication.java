package com.sky.lli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 说明: 主函数入口
 *
 * @author lihao
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class EurekaConsumeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaConsumeApplication.class, args);
    }

}
