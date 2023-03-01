package com.ilearn.verification.service;

import com.ilearn.verification.model.VerificationCodeParamsDto;
import com.ilearn.verification.model.VerificationCodeResultDto;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 验证码接口
 * @date 28/2/2023 5:56 PM
 */
public interface VerificationCodeService {


    /**
     * 生成验证码
     *
     * @param verificationCodeParamsDto 请求验证码参数
     * @return 验证码结果
     */
    VerificationCodeResultDto generate(VerificationCodeParamsDto verificationCodeParamsDto);

    /**
     * 比对验证码
     *
     * @param key  验证码在缓存中的key
     * @param code 前端输入的验证码
     * @return 前端输入的和缓存中的是否匹配
     */
    boolean verify(String key, String code);


    interface VerificationCodeGenerator {
        /**
         * 验证码生成
         *
         * @return 验证码
         */
        String generate(int length);
    }

    interface KeyGenerator {

        /**
         * key生成
         *
         * @return 验证码
         */
        String generate(String prefix);
    }

    interface VerificationCodeStore {

        /**
         * 验证码放入缓存
         *
         * @param key    验证码key
         * @param value  验证码
         * @param expire 过期时间
         */
        void set(String key, String value, Integer expire);

        String get(String key);

        void remove(String key);
    }
}
