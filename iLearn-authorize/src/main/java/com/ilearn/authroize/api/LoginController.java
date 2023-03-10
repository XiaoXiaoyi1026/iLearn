package com.ilearn.authroize.api;

import com.ilearn.users.mapper.UserMapper;
import com.ilearn.users.model.po.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 平台登录
 * @date 2/24/2023 3:35 PM
 */
@Slf4j
@RestController
public class LoginController {

    private UserMapper userMapper;

    @Autowired
    void setIlearnUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @RequestMapping("/login-success")
    public String loginSuccess() {
        return "登录成功";
    }

    @RequestMapping("/user/{id}")
    public User getUser(@PathVariable("id") String id) {
        return userMapper.selectById(id);
    }

    @RequestMapping("/r/r1")
    @PreAuthorize("hasAuthority('p1')")
    public String r1() {
        return "访问r1资源";
    }

    @RequestMapping("/r/r2")
    @PreAuthorize("hasAuthority('p2')")
    public String r2() {
        return "访问r2资源";
    }

}
