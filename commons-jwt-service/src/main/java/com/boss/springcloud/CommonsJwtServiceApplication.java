package com.boss.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CommonsJwtServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonsJwtServiceApplication.class, args);
    }

}
