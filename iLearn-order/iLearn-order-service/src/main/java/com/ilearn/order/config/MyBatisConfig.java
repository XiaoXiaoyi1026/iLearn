package com.ilearn.order.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description MybatisPlus相关配置
 * @date 3/12/2023 2:26 PM
 */
@Configuration
@MapperScan("com.ilearn.order.mapper")
public class MyBatisConfig {

     /*
      * 配置分页拦截器
      * */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 添加分页拦截器
        mybatisPlusInterceptor.addInnerInterceptor(new  PaginationInnerInterceptor(DbType.MYSQL));
        return mybatisPlusInterceptor;
    }

}
