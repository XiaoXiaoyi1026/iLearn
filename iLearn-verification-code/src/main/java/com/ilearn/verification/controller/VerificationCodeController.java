package com.ilearn.verification.controller;

import com.ilearn.verification.model.VerificationCodeParamsDto;
import com.ilearn.verification.model.VerificationCodeResultDto;
import com.ilearn.verification.service.VerificationCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 验证码服务接口
 * @date 28/2/2023 5:56 PM
 */
@Api(value = "验证码服务接口")
@RestController
public class VerificationCodeController {

    @Resource(name = "PicVerificationCodeService")
    private VerificationCodeService picVerificationCodeService;


    @ApiOperation(value = "生成验证信息", notes = "生成验证信息")
    @PostMapping(value = "/pic")
    public VerificationCodeResultDto generatePicVerificationCode(VerificationCodeParamsDto verificationCodeParamsDto) {
        return picVerificationCodeService.generate(verificationCodeParamsDto);
    }

    @ApiOperation(value = "校验", notes = "校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "业务名称", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "key", value = "验证key", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "/verify")
    public Boolean verify(String key, String code) {
        return picVerificationCodeService.verify(key, code);
    }
}
