package com.ilearn.users.model.dto;

import com.ilearn.users.model.po.IlearnUser;
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
public class ILearnUserAuthorities extends IlearnUser {
    /**
     * 授权信息
     */
    private String[] authorities;
}
