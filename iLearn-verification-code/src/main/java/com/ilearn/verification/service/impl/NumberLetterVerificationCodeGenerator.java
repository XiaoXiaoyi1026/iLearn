package com.ilearn.verification.service.impl;

import com.ilearn.verification.service.VerificationCodeService;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 验证码接口
 * @date 28/2/2023 5:56 PM
 */
@Component("NumberLetterVerificationCodeGenerator")
public class NumberLetterVerificationCodeGenerator implements VerificationCodeService.VerificationCodeGenerator {


    @Override
    public String generate(int length) {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(36);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }


}
