package com.ilearn.users.service.impl;

import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.mapper.UserAuthorities;
import com.ilearn.base.mapper.UserRole;
import com.ilearn.users.mapper.IlearnUserMapper;
import com.ilearn.users.model.dto.AuthorizeInfo;
import com.ilearn.users.model.dto.ILearnUserAuthorities;
import com.ilearn.users.model.po.IlearnRole;
import com.ilearn.users.model.po.IlearnUser;
import com.ilearn.users.service.AuthorizeService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 微信登录授权
 * @date 3/4/2023 3:23 PM
 */
@Service(value = "wx_authorize")
public class WXAuthorize implements AuthorizeService {

    private IlearnUserMapper ilearnUserMapper;

    @Autowired
    void setIlearnUserMapper(IlearnUserMapper ilearnUserMapper) {
        this.ilearnUserMapper = ilearnUserMapper;
    }

    @Override
    public ILearnUserAuthorities execute(@NotNull AuthorizeInfo authorizeInfo) {
        // 获取微信的授权码
        Map<String, Object> payload = authorizeInfo.getPayload();
        String code = String.valueOf(payload.get("code"));
        if (code == null) {
            ILearnException.cast("微信授权登录失败");
        }
        // 拿着授权码去申请令牌

        // 用令牌去调用微信的用户信息服务获取用户信息

        // 将用户信息保存进数据库
        IlearnUser ilearnUser = ilearnUserMapper.selectById(1026L);
        ILearnUserAuthorities ilearnUserAuthorities = new ILearnUserAuthorities();
        BeanUtils.copyProperties(ilearnUser, ilearnUserAuthorities);
        IlearnRole role = ilearnUserMapper.getRole(ilearnUser.getId());
        UserRole userRole = UserRole.getUserRole(role.getRoleCode());
        if (userRole == null) {
            ILearnException.cast("获取用户角色失败");
        }
        String[] userAuthorities = UserAuthorities.getUserAuthorities(userRole);
        ilearnUserAuthorities.setAuthorities(userAuthorities);
        // 返回用户的授权信息
        return ilearnUserAuthorities;
    }
}
