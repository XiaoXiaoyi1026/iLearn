package com.ilearn.users.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ilearn.base.mapper.UserAuthorities;
import com.ilearn.base.mapper.UserRole;
import com.ilearn.base.utils.JsonUtil;
import com.ilearn.users.mapper.IlearnUserMapper;
import com.ilearn.users.model.dto.AuthorizeInfo;
import com.ilearn.users.model.po.IlearnRole;
import com.ilearn.users.model.po.IlearnUser;
import com.ilearn.users.service.AuthorizeService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 用户名密码验证
 * @date 2/26/2023 4:28 PM
 */
@Slf4j
@Service
public class PasswordAuthorize implements AuthorizeService {

    private IlearnUserMapper ilearnUserMapper;

    @Autowired
    void setIlearnUserMapper(IlearnUserMapper ilearnUserMapper) {
        this.ilearnUserMapper = ilearnUserMapper;
    }

    @Override
    public UserDetails execute(@NotNull AuthorizeInfo authorizeInfo) {
        // 获取账号信息
        String userName = authorizeInfo.getUserName();
        // 从数据库查询用户信息
        LambdaQueryWrapper<IlearnUser> ilearnUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ilearnUserLambdaQueryWrapper.eq(IlearnUser::getUsername, userName);
        IlearnUser ilearnUser = ilearnUserMapper.selectOne(ilearnUserLambdaQueryWrapper);
        if (ilearnUser == null) {
            // 直接返回空, 交给框架处理
            return null;
        }
        // 比对密码
        String userPassword = ilearnUser.getPassword();
        IlearnRole role = ilearnUserMapper.getRoleCode(ilearnUser.getId());
        if (role == null) {
            log.error("查询用户角色失败, authorizeJsonInfo: {}", authorizeInfo);
            return null;
        }
        String roleCode = role.getRoleCode();
        UserRole userRole = UserRole.getUserRole(roleCode);
        if (userRole == null) {
            log.error("查询用户角色失败, authorizeJsonInfo: {}", authorizeInfo);
            return null;
        }
        // 将密码置空, 防止信息泄露
        ilearnUser.setPassword(null);
        // 将用户信息转成Json字符串
        String userJson = JsonUtil.objectToJson(ilearnUser);
        // 构建用户账号信息密码和权限一并写入jwt令牌上下文返回
        return User.withUsername(userJson).password(userPassword)
                .authorities(UserAuthorities.getUserAuthorities(userRole)).build();
    }
}
