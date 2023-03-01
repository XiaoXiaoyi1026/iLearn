package com.ilearn.verification.service.impl;

import com.ilearn.verification.service.VerificationCodeService;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 验证码接口
 * @date 28/2/2023 5:56 PM
 */
@Component("UUIDKeyGenerator")
public class UUIDKeyGenerator implements VerificationCodeService.KeyGenerator {
    @Override
    public String generate(String prefix) {
        String uuid = UUID.randomUUID().toString();
        return prefix + uuid.replaceAll("-", "");
    }
}
