package com.sky.lli;

import org.jasig.cas.client.boot.configuration.EnableCasClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 说明: 主函数入口
 *
 * @author lihao
 */
@SpringBootApplication
@EnableCasClient
public class CasClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(CasClientApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
