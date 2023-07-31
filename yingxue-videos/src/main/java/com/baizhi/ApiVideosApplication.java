package com.baizhi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ApiVideosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiVideosApplication.class, args);
    }

}
