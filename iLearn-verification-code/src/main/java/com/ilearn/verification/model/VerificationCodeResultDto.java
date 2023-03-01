package com.ilearn.verification.model;

import lombok.Data;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 验证码返回结果
 * @date 28/2/2023 5:56 PM
 */
@Data
public class VerificationCodeResultDto {

    /**
     * key用于验证
     */
    private String key;

    /**
     * 混淆后的内容
     * 举例：
     * 1.图片验证码为:图片base64编码
     * 2.短信验证码为:null
     * 3.邮件验证码为: null
     * 4.邮件链接点击验证为：null
     * ...
     */
    private String aliasing;
}
