package com.ilearn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.ilearn.learning.feign"})
public class LearningCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningCenterApplication.class, args);
    }

}
