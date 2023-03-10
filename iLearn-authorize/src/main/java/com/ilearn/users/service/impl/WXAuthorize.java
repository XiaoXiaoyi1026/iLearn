package com.ilearn.users.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.mapper.UserType;
import com.ilearn.base.utils.JsonUtil;
import com.ilearn.users.mapper.UserMapper;
import com.ilearn.users.mapper.UserRoleMapper;
import com.ilearn.users.model.dto.AuthorizeInfo;
import com.ilearn.users.model.dto.UserAuthorities;
import com.ilearn.users.model.po.User;
import com.ilearn.users.model.po.UserRole;
import com.ilearn.users.service.AuthorizeService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 微信登录授权
 * @date 3/4/2023 3:23 PM
 */
@Slf4j
@Service(value = "wx_authorize")
public class WXAuthorize implements AuthorizeService {

    private UserMapper userMapper;

    private RestTemplate restTemplate;

    private WXAuthorize wXAuthorize;

    private UserRoleMapper userRoleMapper;

    @Value("authorize.appID")
    private String appID;

    @Value("authorize.secret")
    private String secret;

    @Autowired
    void setIlearnUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    void setWXAuthorize(WXAuthorize wXAuthorize) {
        this.wXAuthorize = wXAuthorize;
    }

    @Autowired
    void setIlearnUserRoleMapper(UserRoleMapper userRoleMapper) {
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAuthorities execute(@NotNull AuthorizeInfo authorizeInfo) {
        // 获取微信的授权码
        Map<String, Object> payload = authorizeInfo.getPayload();
        String code = String.valueOf(payload.get("code"));
        if (code == null) {
            ILearnException.cast("微信授权登录失败");
        }
        // 拿着授权码去申请令牌
        Map<String, Object> wxAccessToken = getWXAccessToken(code);
        // 用令牌去调用微信的用户信息服务获取用户信息
        String accessToken = String.valueOf(wxAccessToken.get("access_token"));
        String openid = String.valueOf(wxAccessToken.get("openid"));
        if (accessToken == null || openid == null) {
            ILearnException.cast("微信授权登录失败");
        }
        Map<String, Object> userInfo = getUserInfo(accessToken, openid);
        if (userInfo == null) {
            ILearnException.cast("获取用户信息失败");
        }
        // 将用户信息保存进数据库(涉及多个表的操作, 为保证数据一致性需要加上事务控制)
        User user = wXAuthorize.saveUserInfo2DB(userInfo);
        UserAuthorities ilearnUserAuthorities = new UserAuthorities();
        BeanUtils.copyProperties(user, ilearnUserAuthorities);
        // 返回用户的授权信息
        return ilearnUserAuthorities;
    }

    /**
     * 保存用户信息到数据库
     *
     * @param userInfo 微信用户信息
     *                 参数            说明
     *                 openid        普通用户的标识，对当前开发者帐号唯一
     *                 nickname        普通用户昵称
     *                 sex            普通用户性别，1为男性，2为女性
     *                 province        普通用户个人资料填写的省份
     *                 city            普通用户个人资料填写的城市
     *                 country        国家，如中国为CN
     *                 headimgurl        用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
     *                 privilege        用户特权信息，json数组，如微信沃卡用户为（chinaunicom）
     *                 unionid          用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的 unionid 是唯一的。
     * @return 保存结果
     */
    @Transactional(rollbackFor = Exception.class)
    public User saveUserInfo2DB(@NotNull Map<String, Object> userInfo) {
        // 先取出unionID
        String unionID = String.valueOf(userInfo.get("unionid"));
        LambdaQueryWrapper<User> ilearnUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ilearnUserLambdaQueryWrapper.eq(User::getWxUnionid, unionID);
        User user = userMapper.selectOne(ilearnUserLambdaQueryWrapper);
        if (user == null) {
            // 用户不存在则添加
            user = new User();
            String userId = String.valueOf(UUID.randomUUID());
            user.setId(userId);
            user.setWxUnionid(unionID);
            String nickname = String.valueOf(userInfo.get("nickname"));
            user.setNickname(nickname);
            user.setName(nickname);
            user.setUserpic(String.valueOf(userInfo.get("headimgurl")));
            user.setPassword(unionID);
            LocalDateTime createTime = LocalDateTime.now();
            user.setCreateTime(createTime);
            user.setSex(String.valueOf(userInfo.get("sex")));
            user.setStatus("1");
            user.setUtype(UserType.get(com.ilearn.base.mapper.UserRole.STUDENT));
            int insert = userMapper.insert(user);
            if (insert == 0) {
                // 如果保存用户信息失败, 需要抛出异常给框架以进行事务控制
                log.error("保存用户信息到数据库失败, userInfo: {}", userInfo);
                throw new RuntimeException("保存用户信息到数据库失败");
            }
            // 保存用户角色相关信息, 数据库中学生角色的id为17
            String studentRoleId = "17";
            UserRole userRole = new UserRole();
            userRole.setId(String.valueOf(UUID.randomUUID()));
            userRole.setUserId(userId);
            userRole.setCreateTime(createTime);
            userRole.setCreator("超级管理员");
            userRole.setRoleId(studentRoleId);
            if (userRoleMapper.insert(userRole) == 0) {
                log.error("保存用户和角色的关联信息失败, userRole: " + userRole);
                throw new RuntimeException("保存用户和角色的关联信息失败");
            }
        }
        return user;
    }

    /**
     * 使用令牌请求微信用户服务获取用户信息
     *
     * @param accessToken 令牌
     * @param openid      开放id
     * @return 用户信息
     */
    private Map<String, Object> getUserInfo(String accessToken, String openid) {
        String urlTemplate = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        String url = String.format(urlTemplate, accessToken, openid);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        String body = response.getBody();
        return JsonUtil.jsonToMap(body);
    }

    /**
     * 获取请求微信服务用的令牌
     *
     * @param code 用户授权码
     * @return 微信令牌
     */
    private Map<String, Object> getWXAccessToken(String code) {
        // url模板
        String urlTemplate = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        // 向模板中填充数据
        String url = String.format(urlTemplate, appID, secret, code);
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
