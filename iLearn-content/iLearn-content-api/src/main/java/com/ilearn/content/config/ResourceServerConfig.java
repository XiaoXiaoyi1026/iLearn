package com.ilearn.content.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 资源服务配置(认证授权)
 * @date 2/25/2023 3:34 PM
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {


    /**
     * 资源名称(和认证授权服务中的AuthorizationServer中颁发令牌时的资源名称一致)
     */
    public static final String RESOURCE_ID = "ilearn";

    private TokenStore tokenStore;

    @Autowired
    void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public void configure(@NotNull ResourceServerSecurityConfigurer resources) {
        //资源 id
        resources.resourceId(RESOURCE_ID).tokenStore(tokenStore).stateless(true);
    }

    @Override
    public void configure(@NotNull HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                // 所有/r/**或者是/course/**的请求必须认证通过
                .antMatchers("/r/**", "/course/**").authenticated()
                // 其余路径的请求全部放行
                .anyRequest().permitAll();
    }

}
