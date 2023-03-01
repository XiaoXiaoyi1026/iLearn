package com.ilearn.verification.service.impl;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.ilearn.verification.service.AbstractVerificationCodeService;
import com.ilearn.base.utils.EncryptUtil;
import com.ilearn.verification.model.VerificationCodeParamsDto;
import com.ilearn.verification.model.VerificationCodeResultDto;
import com.ilearn.verification.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Mr.M
 * @version 1.0
 * @description 图片验证码生成器
 * @date 2022/9/29 16:16
 */
@Service("PicVerificationCodeService")
public class PicVerificationCodeServiceImpl extends AbstractVerificationCodeService implements VerificationCodeService {


    private DefaultKaptcha kaptcha;

    @Autowired
    void setKaptcha(DefaultKaptcha kaptcha) {
        this.kaptcha = kaptcha;
    }

    @Resource(name = "NumberLetterVerificationCodeGenerator")
    @Override
    public void setVerificationCodeGenerator(VerificationCodeGenerator verificationCodeGenerator) {
        this.verificationCodeGenerator = verificationCodeGenerator;
    }

    @Resource(name = "UUIDKeyGenerator")
    @Override
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }


    @Resource(name = "MemoryVerificationCodeStore")
    @Override
    public void setVerificationCodeStore(VerificationCodeStore verificationCodeStore) {
        this.verificationCodeStore = verificationCodeStore;
    }


    @Override
    public VerificationCodeResultDto generate(VerificationCodeParamsDto verificationCodeParamsDto) {
        GenerateResult generate = generate(verificationCodeParamsDto, 4, "verificationCode:", 60);
        String key = generate.getKey();
        String code = generate.getCode();
        String pic = createPic(code);
        VerificationCodeResultDto verificationCodeResultDto = new VerificationCodeResultDto();
        verificationCodeResultDto.setAliasing(pic);
        verificationCodeResultDto.setKey(key);
        return verificationCodeResultDto;

    }

    private String createPic(String code) {
        // 生成图片验证码
        ByteArrayOutputStream outputStream;
        BufferedImage image = kaptcha.createImage(code);

        outputStream = new ByteArrayOutputStream();
        String imgBase64Encoder = null;
        try {
            // 对字节数组Base64编码
            ImageIO.write(image, "png", outputStream);
            imgBase64Encoder = "data:image/png;base64," + EncryptUtil.encodeBase64(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgBase64Encoder;
    }
}
