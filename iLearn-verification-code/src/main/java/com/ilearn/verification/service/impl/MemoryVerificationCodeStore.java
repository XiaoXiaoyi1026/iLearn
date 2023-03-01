package com.ilearn.verification.service.impl;

import com.ilearn.verification.service.VerificationCodeService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 验证码接口
 * @date 28/2/2023 5:56 PM
 */
@Component("MemoryVerificationCodeStore")
public class MemoryVerificationCodeStore implements VerificationCodeService.VerificationCodeStore {

    Map<String, String> map = new HashMap<>();

    @Override
    public void set(String key, String value, Integer expire) {
        map.put(key, value);
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }
}
