package com.ilearn.gateway.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description Security配置
 * @date 2023/2/26 9:50
 */
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {
    /**
     * 安全拦截配置
     *
     * @param http http请求
     * @return 过滤器链
     */
    @Bean
    public SecurityWebFilterChain webFluxSecurityFilterChain(@NotNull ServerHttpSecurity http) {
        return http.authorizeExchange()
                .pathMatchers("/**").permitAll()
                .anyExchange().authenticated()
                .and().csrf().disable().build();
    }
}
