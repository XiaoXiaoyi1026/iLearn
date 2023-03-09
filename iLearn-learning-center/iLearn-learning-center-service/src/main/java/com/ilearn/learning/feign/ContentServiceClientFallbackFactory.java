package com.ilearn.learning.feign;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 熔断降级工厂
 * @date 3/9/2023 2:27 PM
 */
@Component
@Slf4j
public class ContentServiceClientFallbackFactory implements FallbackFactory<ContentServiceClient> {
    @Override
    public ContentServiceClient create(Throwable cause) {
        return courseId -> {
            // 记录错误日志
            log.error("远程查询发布课程预览服务发生降级操作, 因为: {}", cause.getMessage());
            return null;
        };
    }
}
