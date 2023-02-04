package com.ilearn;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 内容管理启动类
 * @date 1/24/2023 5:26 PM
 */
@EnableSwagger2Doc
@SpringBootApplication
public class ContentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentApiApplication.class, args);
    }

}
