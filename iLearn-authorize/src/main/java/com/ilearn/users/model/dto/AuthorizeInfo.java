package com.ilearn.users.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 统一认证需要的信息
 * @date 2/26/2023 3:51 PM
 */
@Data
@ApiModel(value = "认证信息", description = "认证信息中的参数")
public class AuthorizeInfo {

    /**
     * 用户名
     */
    @ApiModelProperty(name = "用户名")
    private String userName;

    /**
     * 密码
     */
    @ApiModelProperty(name = "密码")
    private String password;

    /**
     * 手机
     */
    @ApiModelProperty(name = "电话号码")
    private String cellphone;

    /**
     * 验证码
     */
    @ApiModelProperty(name = "验证码")
    private String verificationCode;

    /**
     * 验证码key, 用于去redis中取验证码进行校验
     */
    @ApiModelProperty(name = "验证码key, 用于去redis中取验证码进行校验")
    private String verificationCodeKey;

    /**
     * 授权类型
     */
    @ApiModelProperty(name = "授权类型", required = true)
    private String authorizeType;

    /**
     * 附加信息, 不同的认证形式有不同的附加信息
     */
    private Map<String, Object> payload;

    public AuthorizeInfo() {
        payload = new HashMap<>();
    }

}
