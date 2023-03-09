package com.ilearn.content.utils;

import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.utils.JsonUtil;
import com.ilearn.content.model.po.IlearnUser;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 认证工具
 * @date 2/26/2023 2:46 PM
 */
@Slf4j
public class SecurityUtil {
    /**
     * 从认证上下文获取认证信息
     *
     * @param infoClass 目标对象
     * @param <T>       泛型
     * @return 对象
     */
    public static <T> @Nullable T getInfoFromSecurityContext(Class<T> infoClass) {
        // 从令牌中获取认证信息(对象信息的Json字符串)
        Object info = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (info == null) {
            log.error("认证信息为空");
            ILearnException.cast("认证信息无效");
        }
        if (info instanceof String) {
            T object = null;
            try {
                // 将用户信息的Json字符串转回对象信息
                object = JsonUtil.jsonToObject((String) info, infoClass);
            } catch (Exception e) {
                log.error("认证信息转换对象失败, info: {}", info);
            }
            return object;
        }
        return null;
    }

    /**
     * 获取登录用户的授权信息
     *
     * @return 授权信息
     */
    public static IlearnUser getSecurityInfo() {
        IlearnUser userInfo = SecurityUtil.getInfoFromSecurityContext(IlearnUser.class);
        if (userInfo == null) {
            ILearnException.cast("授权信息异常, 请重新登录后再试");
        }
        return userInfo;
    }
}
