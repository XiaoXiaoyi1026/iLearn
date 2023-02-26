package com.ilearn.users.service.impl;

import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.mapper.AuthorizeType;
import com.ilearn.base.utils.JsonUtil;
import com.ilearn.users.Authorize;
import com.ilearn.users.model.dto.AuthorizeInfo;
import lombok.extern.slf4j.Slf4j;
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
        String authorizeType = authorizeInfo.getAuthorizeType();
        switch (authorizeType) {
            case AuthorizeType.PASSWORD:
                // 策略模式调用密码验证服务
                return new Authorize(new PasswordAuthorize()).execute(authorizeInfo);
            case AuthorizeType.SMS:
                return null;
            case AuthorizeType.WX:
                return null;
            default:
                ILearnException.cast("授权类型错误");
        }
        return null;
    }
}
