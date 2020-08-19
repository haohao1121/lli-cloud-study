package com.sky.lli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 说明: 主函数入口
 *
 * @author lihao
 */
@SpringBootApplication
@EnableEurekaClient
public class EurekaProvideApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaProvideApplication.class, args);
    }

}
