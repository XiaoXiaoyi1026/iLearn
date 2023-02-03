package com.ilearn;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 内容管理启动类
 * @date 1/24/2023 5:26 PM
 */
@EnableSwagger2Doc
@SpringBootApplication
public class ContentApiApplication {

    @Value("${test-config.a}")
    private String a;
    @Value("${test-config.b}")
    private String b;
    @Value("${test-config.c}")
    private String c;
    @Value("${test-config.d}")
    private String d;

    /**
     * 获取测试配置文件的内容
     *
     * @return 配置文件的测试编号
     */
    @Bean
    public Integer getTest() {
        // 测试结果: 远程配置 > 扩展远程配置 > 通用远程配置 > 本地配置
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);
        System.out.println("d = " + d);
        return 1;
    }

    public static void main(String[] args) {
        SpringApplication.run(ContentApiApplication.class, args);
    }

}
