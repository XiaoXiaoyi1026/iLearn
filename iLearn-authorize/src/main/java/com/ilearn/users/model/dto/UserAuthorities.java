package com.ilearn.users.model.dto;

import com.ilearn.users.model.po.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 用户信息扩展类
 * @date 2/26/2023 5:01 PM
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserAuthorities extends User {
    /**
     * 授权信息
     */
    private String[] authorities;
}
