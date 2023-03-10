package com.ilearn.users.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.utils.StringUtil;
import com.ilearn.users.feign.VerificationCodeFeign;
import com.ilearn.users.mapper.UserMapper;
import com.ilearn.users.model.dto.AuthorizeInfo;
import com.ilearn.users.model.dto.UserAuthorities;
import com.ilearn.users.model.po.User;
import com.ilearn.users.service.AuthorizeService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 用户名密码验证
 * @date 2/26/2023 4:28 PM
 */
@Slf4j
@Service("password_authorize")
public class PasswordAuthorize implements AuthorizeService {

    private UserMapper userMapper;

    /**
     * 密码验证器
     */
    private PasswordEncoder passwordEncoder;

    /**
     * 远程调用验证码验证服务的Feign客户端
     */
    private VerificationCodeFeign verificationCodeFeign;

    @Autowired
    void setIlearnUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    void setVerificationCodeFeign(VerificationCodeFeign verificationCodeFeign) {
        this.verificationCodeFeign = verificationCodeFeign;
    }

    @Override
    public UserAuthorities execute(@NotNull AuthorizeInfo authorizeInfo) {
        // 校验验证码
        String verificationCode = authorizeInfo.getVerificationCode();
        String verificationCodeKey = authorizeInfo.getVerificationCodeKey();
        if (StringUtil.isBlank(verificationCode) || StringUtil.isBlank(verificationCodeKey)) {
            ILearnException.cast("验证码为空");
        }
        Boolean verify = verificationCodeFeign.verify(verificationCodeKey, verificationCode);
        if (verify == null) {
            ILearnException.cast("服务繁忙, 请稍后再试");
        }
        if (!verify) {
            ILearnException.cast("验证码错误");
        }
        // 获取账号信息
        String userName = authorizeInfo.getUserName();
        // 从数据库查询用户信息
        LambdaQueryWrapper<User> ilearnUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ilearnUserLambdaQueryWrapper.eq(User::getUsername, userName);
        User user = userMapper.selectOne(ilearnUserLambdaQueryWrapper);
        if (user == null) {
            log.error("用户{}不存在", userName);
            ILearnException.cast("用户" + userName + "不存在");
        }
        // 原密码
        String userPassword = user.getPassword();
        // 输入的密码
        String inputPassword = authorizeInfo.getPassword();
        // 调用密码验证器进行验证
        boolean matches = passwordEncoder.matches(inputPassword, userPassword);
        if (!matches) {
            ILearnException.cast("密码错误");
        }
        // 密码置空, 保证信息安全
        user.setPassword(null);
        UserAuthorities userAuthorities = new UserAuthorities();
        BeanUtils.copyProperties(user, userAuthorities);
        return userAuthorities;
    }
}
