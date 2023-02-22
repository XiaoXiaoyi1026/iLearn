package com.ilearn.content.feign;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description Feign远程调用媒体服务的降级Factory
 * @date 2/21/2023 3:35 PM
 */
@Slf4j
@Component
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable throwable) {
        // 通过FallbackFactory可以拿到下游熔断的错误信息
        return (fileData, folder, objectName) -> {
            // 降级方法, 可以记录错误日志
            log.debug("Feign远程调用iLearn-media-api服务出现熔断异常, 异常信息: {}", throwable.getMessage());
            // 快速返回, 避免上游服务被下游牵连(雪崩)
            return null;
        };
    }
}
