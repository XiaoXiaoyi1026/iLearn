package com.ilearn.verification.service;

import com.ilearn.verification.model.VerificationCodeParamsDto;
import com.ilearn.verification.model.VerificationCodeResultDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 抽象验证码服务
 * @date 28/2/2023 5:56 PM
 */
@Slf4j
public abstract class AbstractVerificationCodeService implements VerificationCodeService {

    protected VerificationCodeGenerator verificationCodeGenerator;

    protected KeyGenerator keyGenerator;

    protected VerificationCodeStore verificationCodeStore;


    public abstract void setVerificationCodeGenerator(VerificationCodeGenerator verificationCodeGenerator);

    public abstract void setKeyGenerator(KeyGenerator keyGenerator);

    public abstract void setVerificationCodeStore(VerificationCodeStore verificationCodeStore);


    /**
     * 生成验证码
     *
     * @param verificationCodeParamsDto 验证码请求参数
     * @param code_length               验证码长度
     * @param keyPrefix                 前缀
     * @param expire                    过期时间
     * @return 验证码
     */
    public GenerateResult generate(VerificationCodeParamsDto verificationCodeParamsDto, Integer code_length, String keyPrefix, Integer expire) {
        //生成四位验证码
        String code = verificationCodeGenerator.generate(code_length);
        log.debug("生成验证码:{}", code);
        //生成一个key
        String key = keyGenerator.generate(keyPrefix);

        //存储验证码
        verificationCodeStore.set(key, code, expire);
        //返回验证码生成结果
        GenerateResult generateResult = new GenerateResult();
        generateResult.setKey(key);
        generateResult.setCode(code);
        return generateResult;
    }

    @Data
    protected static class GenerateResult {
        String key;
        String code;
    }


    public abstract VerificationCodeResultDto generate(VerificationCodeParamsDto verificationCodeParamsDto);


    public boolean verify(String key, String code) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(code)) {
            return false;
        }
        String code_l = verificationCodeStore.get(key);
        if (code_l == null) {
            return false;
        }
        boolean result = code_l.equalsIgnoreCase(code);
        if (result) {
            //删除验证码
            verificationCodeStore.remove(key);
        }
        return result;
    }


}
