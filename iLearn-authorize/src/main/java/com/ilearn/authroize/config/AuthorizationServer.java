package com.ilearn.authroize.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import javax.annotation.Resource;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 认证服务器相关配置(负责颁发令牌)
 * @date 1/25/2023 2:26 PM
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Resource(name = "authorizationServerTokenServicesCustom")
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    private AuthenticationManager authenticationManager;

    private PasswordEncoder passwordEncoder;

    @Autowired
    void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 客户端详情服务
     *
     * @param clients 客户端
     * @throws Exception 错误信息
     */
    @Override
    public void configure(@NotNull ClientDetailsServiceConfigurer clients)
            throws Exception {
        // 使用in-memory存储
        clients.inMemory()
                // client_id: 客户端id
                .withClient("ILearnWebApp")
                // 客户端密钥(使用BCrypt加密后)
                .secret(passwordEncoder.encode("ILearn1026"))
                // 资源名称
                .resourceIds("ilearn")
                // 该client允许的授权类型authorization_code, password, refresh_token, implicit, client_credentials
                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token")
                // 允许的授权范围
                .scopes("all")
                // false跳转到授权页面
                .autoApprove(false)
                // 客户端接收授权码的重定向地址
                .redirectUris("http://www.ilearn1026.com")
        ;
    }


    /**
     * 令牌端点的访问配置
     *
     * @param endpoints 端点
     */
    @Override
    public void configure(@NotNull AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                // 认证管理器
                .authenticationManager(authenticationManager)
                // 令牌管理服务
                .tokenServices(authorizationServerTokenServices)
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }

    /**
     * 令牌端点的安全配置
     *
     * @param security 安全配置
     */
    @Override
    public void configure(@NotNull AuthorizationServerSecurityConfigurer security) {
        security
                // oauth/token_key是公开
                .tokenKeyAccess("permitAll()")
                // oauth/check_token公开
                .checkTokenAccess("permitAll()")
                // 表单认证(申请令牌)
                .allowFormAuthenticationForClients()
        ;
    }

}
