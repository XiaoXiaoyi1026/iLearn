package com.ilearn.authroize.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * 自定义DaoAuthenticationProvider, 用于进行统一认证服务
 *
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 自定义DaoAuthenticationProvider
 * @date 2/26/2023 3:33 PM
 */
@Slf4j
@Component
public class DaoAuthenticationProviderCustom extends DaoAuthenticationProvider {

    /**
     * 设置DaoAuthenticationProvider的用户信息服务, 从Spring容器中拿
     *
     * @param userDetailsService 用户信息服务
     */
    @Override
    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }

    /**
     * 重写主类的密码验证方法, 使其支持其他验证方式
     *
     * @param userDetails    认证信息
     * @param authentication the current request that needs to be authenticated
     * @throws AuthenticationException 身份验证异常
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        System.out.println(userDetails);
        System.out.println(authentication);
    }
}
