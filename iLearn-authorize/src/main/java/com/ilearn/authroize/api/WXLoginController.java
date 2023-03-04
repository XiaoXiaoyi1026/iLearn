package com.ilearn.authroize.api;

import com.ilearn.users.model.dto.AuthorizeInfo;
import com.ilearn.users.model.dto.ILearnUserAuthorities;
import com.ilearn.users.service.AuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 微信登录授权接口
 * @date 3/4/2023 3:21 PM
 */
@Controller
public class WXLoginController {

    private ApplicationContext applicationContext;

    @Autowired
    void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @RequestMapping("/wxLogin")
    public String wxLogin(String code, String state) {
        // 使用授权码去获取用户信息
        AuthorizeService wxAuthorize = applicationContext.getBean("wx_authorize", AuthorizeService.class);
        AuthorizeInfo authorizeInfo = new AuthorizeInfo();
        Map<String, Object> payload = authorizeInfo.getPayload();
        payload.put("code", code);
        payload.put("state", state);
        ILearnUserAuthorities userExtension = wxAuthorize.execute(authorizeInfo);
        if (userExtension == null) {
            // 如果获取用户信息失败, 则重定向到错误页面
            return "redirect:http://http://www.ilearn1026.com/error.html";
        }
        // 否则重定向到登录页面进行登录
        return "redirect:http://www.ilearn1026.com/sign.html?username=" + userExtension.getUsername() + "&authorizeType=wx";
    }

}
