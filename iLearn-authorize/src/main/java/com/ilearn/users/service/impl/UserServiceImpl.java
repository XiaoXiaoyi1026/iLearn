package com.ilearn.users.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ilearn.base.dictionary.UserAuthorities;
import com.ilearn.base.dictionary.UserRole;
import com.ilearn.base.utils.JsonUtil;
import com.ilearn.users.mapper.IlearnUserMapper;
import com.ilearn.users.model.po.IlearnRole;
import com.ilearn.users.model.po.IlearnUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private IlearnUserMapper ilearnUserMapper;

    @Autowired
    void setIlearnUserMapper(IlearnUserMapper ilearnUserMapper) {
        this.ilearnUserMapper = ilearnUserMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查询用户信息
        LambdaQueryWrapper<IlearnUser> ilearnUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ilearnUserLambdaQueryWrapper.eq(IlearnUser::getUsername, username);
        IlearnUser ilearnUser = ilearnUserMapper.selectOne(ilearnUserLambdaQueryWrapper);
        if (ilearnUser == null) {
            // 直接返回空, 交给框架处理
            return null;
        }
        // 比对密码
        String userPassword = ilearnUser.getPassword();
        IlearnRole role = ilearnUserMapper.getRoleCode(ilearnUser.getId());
        if (role == null) {
            log.error("查询用户角色失败, username: {}", username);
            return null;
        }
        String roleCode = role.getRoleCode();
        UserRole userRole = UserRole.getUserRole(roleCode);
        if (userRole == null) {
            log.error("查询用户角色失败, username: {}", username);
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
