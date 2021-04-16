package com.sky.lli;

import org.jasig.cas.client.boot.configuration.EnableCasClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 说明: 主函数入口
 *
 * @author lihao
 */
@SpringBootApplication
@EnableCasClient
public class CasClientOneApplication {
    public static void main(String[] args) {
        SpringApplication.run(CasClientOneApplication.class, args);
    }
}
