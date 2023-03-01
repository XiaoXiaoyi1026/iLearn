package com.ilearn.verification.model;

import lombok.Data;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 请求验证码参数
 * @date 28/2/2023 5:56 PM
 */
@Data
public class VerificationCodeParamsDto {

    /**
     * 验证码类型:pic、sms、email等
     */
    private String VerificationCodeType;

    /**
     * 业务携带参数
     */
    private String param1;
    private String param2;
    private String param3;
}
