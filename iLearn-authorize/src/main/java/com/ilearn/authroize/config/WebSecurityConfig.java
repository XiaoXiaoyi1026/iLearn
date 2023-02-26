package com.ilearn.authroize.config;

import com.ilearn.authroize.api.DaoAuthenticationProviderCustom;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 安全管理配置
 * @date 2023/2/24 16:33
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 自定义的认证提供者
     */
    private DaoAuthenticationProviderCustom daoAuthenticationProviderCustom;

    @Autowired
    void setDaoAuthenticationProvider(DaoAuthenticationProviderCustom daoAuthenticationProvider) {
        this.daoAuthenticationProviderCustom = daoAuthenticationProvider;
    }

    @Override
    protected void configure(@NotNull AuthenticationManagerBuilder authenticationManagerBuilder) {
        // 配置框架使用自动以的认证提供者
        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProviderCustom);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用BCrypt对密码进行加密, 单向Hash加密算法, 每一次加密都使用随机盐对明文进行加密
        return new BCryptPasswordEncoder();
    }

    //配置安全拦截机制
    @Override
    protected void configure(@NotNull HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // 访问/r开始的请求需要认证通过
                .antMatchers("/r/**").authenticated()
                // 其它请求全部放行
                .anyRequest().permitAll()
                .and()
                // 登录成功跳转到/login-success
                .formLogin().successForwardUrl("/login-success");

        // 退出登录
        http.logout().logoutUrl("/logout");
    }
}
