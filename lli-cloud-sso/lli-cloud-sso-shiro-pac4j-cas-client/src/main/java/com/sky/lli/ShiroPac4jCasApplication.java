package com.sky.lli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 描述:
 *
 * @author lihao
 * @date 2021/4/25
 */
@SpringBootApplication
public class ShiroPac4jCasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiroPac4jCasApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
