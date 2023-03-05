package com.ilearn.users.service.impl;

import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.mapper.UserAuthorities;
import com.ilearn.base.mapper.UserRole;
import com.ilearn.base.utils.JsonUtil;
import com.ilearn.users.mapper.IlearnUserMapper;
import com.ilearn.users.model.dto.AuthorizeInfo;
import com.ilearn.users.model.dto.ILearnUserAuthorities;
import com.ilearn.users.model.po.IlearnRole;
import com.ilearn.users.model.po.IlearnUser;
import com.ilearn.users.service.AuthorizeService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    private RestTemplate restTemplate;

    @Value("authorize.appID")
    private String appID;

    @Value("authorize.secret")
    private String secret;

    @Autowired
    void setIlearnUserMapper(IlearnUserMapper ilearnUserMapper) {
        this.ilearnUserMapper = ilearnUserMapper;
    }

    @Autowired
    void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
        Map<?, ?> wxAccessToken = getWXAccessToken(code);
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

    /**
     * 获取请求微信服务用的令牌
     *
     * @param code 用户授权码
     * @return 微信令牌
     */
    private Map<?, ?> getWXAccessToken(String code) {
        // url模板
        String url_template = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        // 向模板中填充数据
        String url = String.format(url_template, appID, secret, code);
        /* 使用restTemplate远程调用微信服务拿到令牌, 返回结果:
        {
            "access_token":"ACCESS_TOKEN",
            "expires_in":7200,
            "refresh_token":"REFRESH_TOKEN",
            "openid":"OPENID",
            "scope":"SCOPE",
            "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
        } */
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        String body = response.getBody();
        // 将json数据转成Map
        return JsonUtil.jsonToMap(body);
    }
}
