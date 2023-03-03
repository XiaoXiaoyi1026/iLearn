package com.ilearn.users.feign;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 降级工厂
 * @date 3/1/2023 6:35 PM
 */
@Slf4j
@Component
public class VerificationCodeFeignFallbackFactory implements FallbackFactory<VerificationCodeFeign> {
    @Override
    public VerificationCodeFeign create(Throwable cause) {
        return (key, code) -> {
            log.error("远程调用验证码验证服务发生熔断异常: {}", cause.getMessage());
            // 返回空和验证失败返回的false作区分
            return null;
        };
    }
}
