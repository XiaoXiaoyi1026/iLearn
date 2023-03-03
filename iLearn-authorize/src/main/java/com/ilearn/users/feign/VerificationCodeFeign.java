package com.ilearn.users.feign;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 验证码远程客户端
 * @date 3/1/2023 6:33 PM
 */
@FeignClient(name = "iLearn-verification-code", fallbackFactory = VerificationCodeFeignFallbackFactory.class, path = "/checkcode")
public interface VerificationCodeFeign {

    @ApiOperation(value = "校验", notes = "校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "业务名称", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "key", value = "验证key", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "/verify")
    Boolean verify(@RequestParam(name = "key") String key, @RequestParam(name = "code") String code);

}
