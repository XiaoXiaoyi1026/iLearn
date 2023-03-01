package com.ilearn.users.service.impl;

import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.utils.JsonUtil;
import com.ilearn.users.model.dto.AuthorizeInfo;
import com.ilearn.users.model.dto.ILearnUserExtension;
import com.ilearn.users.service.AuthorizeService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 查询用户信息服务
 * @date 2/26/2023 10:37 AM
 */
@Slf4j
@Service
public class UserServiceImpl implements UserDetailsService {

    private ApplicationContext applicationContext;

    @Autowired
    void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 验证方法
     *
     * @param authorizeJsonInfo AuthorizeInfoDto的json串
     * @return 用户详情
     * @throws UsernameNotFoundException 用户未发现
     */
    @Override
    public UserDetails loadUserByUsername(String authorizeJsonInfo) throws UsernameNotFoundException {
        // 解析授权信息
        AuthorizeInfo authorizeInfo = null;
        try {
            authorizeInfo = JsonUtil.jsonToObject(authorizeJsonInfo, AuthorizeInfo.class);
        } catch (Exception e) {
            log.error("授权信息: {}, 解析失败", authorizeJsonInfo);
            ILearnException.cast("授权信息解析失败");
        }
        assert authorizeInfo != null;
        String authorizeType = authorizeInfo.getAuthType();
        // 根据授权类型从Spring容器中拿对应的授权服务的Bean
        AuthorizeService authorizeService = applicationContext.getBean(authorizeType + "_authorize", AuthorizeService.class);
        ILearnUserExtension iLearnUserExtension = authorizeService.execute(authorizeInfo);
        return getUserPrincipal(iLearnUserExtension);
    }

    /**
     * 根据iLearnUserExtension的信息构建UserDetails
     *
     * @param iLearnUserExtension 用户信息
     * @return 框架需要的对象
     */
    private UserDetails getUserPrincipal(@NotNull ILearnUserExtension iLearnUserExtension) {
        return User.withUsername(JsonUtil.objectToJson(iLearnUserExtension))
                // 自定义的DaoAuthenticationProviderCustom不做密码验证
                .password("")
                .authorities(iLearnUserExtension.getAuthorities()).build();
    }
}
