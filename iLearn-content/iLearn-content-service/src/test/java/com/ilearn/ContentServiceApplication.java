package com.ilearn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 内容服务启动类
 * @date 1/25/2023 2:39 PM
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.ilearn.content.feign"})
public class ContentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentServiceApplication.class, args);
    }

}
