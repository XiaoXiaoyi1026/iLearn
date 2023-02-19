package com.ilearn.task.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description MybatisPlus相关配置
 * @date 2/19/2023 10:33 AM
 */
@Configuration(value = "task_mybatisPlus_config")
@MapperScan("com.ilearn.task.mapper")
public class MybatisPlusConfig {


}
