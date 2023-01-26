package com.ilearn.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 本地跨域请求过滤器
 * @date 1/26/2023 4:31 PM
 */
@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter getCorsFilter() {
        // 创建CorsConfiguration
        CorsConfiguration configuration = new CorsConfiguration();

        // 配置哪些方法可以跨域, *代表所有请求方法都可以
        configuration.addAllowedMethod(HttpMethod.GET);

        // 配置哪些请求来源可以跨域
        configuration.addAllowedOrigin("http://localhost:8601");

        // 配置哪些请求头可以跨域
        configuration.addAllowedHeader("*");

        // 配置允许跨域发送cookie
        configuration.setAllowCredentials(true);

        // 创建根据Url地址的拦截器
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();

        // 配置要拦截的地址, /**代表要拦截所有请求
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

}
