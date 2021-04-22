package com.sky.lli;

import org.jasig.cas.client.boot.configuration.EnableCasClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 * @author lihao
 */

@SpringBootApplication
@EnableCasClient
public class SpringSecurityCasClient {
    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityCasClient.class, args);
    }
}
