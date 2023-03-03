package com.ilearn.users.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.mapper.UserAuthorities;
import com.ilearn.base.mapper.UserRole;
import com.ilearn.base.utils.StringUtil;
import com.ilearn.users.feign.VerificationCodeFeign;
import com.ilearn.users.mapper.IlearnUserMapper;
import com.ilearn.users.model.dto.AuthorizeInfo;
import com.ilearn.users.model.dto.ILearnUserExtension;
import com.ilearn.users.model.po.IlearnRole;
import com.ilearn.users.model.po.IlearnUser;
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

    private IlearnUserMapper ilearnUserMapper;

    /**
     * 密码验证器
     */
    private PasswordEncoder passwordEncoder;

    /**
     * 远程调用验证码验证服务的Feign客户端
     */
    private VerificationCodeFeign verificationCodeFeign;

    @Autowired
    void setIlearnUserMapper(IlearnUserMapper ilearnUserMapper) {
        this.ilearnUserMapper = ilearnUserMapper;
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
    public ILearnUserExtension execute(@NotNull AuthorizeInfo authorizeInfo) {
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
        LambdaQueryWrapper<IlearnUser> ilearnUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ilearnUserLambdaQueryWrapper.eq(IlearnUser::getUsername, userName);
        IlearnUser ilearnUser = ilearnUserMapper.selectOne(ilearnUserLambdaQueryWrapper);
        if (ilearnUser == null) {
            log.error("用户{}不存在", userName);
            ILearnException.cast("用户" + userName + "不存在");
        }
        // 原密码
        String userPassword = ilearnUser.getPassword();
        // 输入的密码
        String inputPassword = authorizeInfo.getPassword();
        // 调用密码验证器进行验证
        boolean matches = passwordEncoder.matches(inputPassword, userPassword);
        if (!matches) {
            ILearnException.cast("密码错误");
        }
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
        // 密码置空, 保证信息安全
        ilearnUser.setPassword(null);
        ILearnUserExtension iLearnUserExtension = new ILearnUserExtension();
        BeanUtils.copyProperties(ilearnUser, iLearnUserExtension);
        // 设置用户权限
        iLearnUserExtension.setAuthorities(UserAuthorities.getUserAuthorities(userRole));
        return iLearnUserExtension;
    }
}
