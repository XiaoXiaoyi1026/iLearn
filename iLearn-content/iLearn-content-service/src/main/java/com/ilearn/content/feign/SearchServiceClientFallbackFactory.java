package com.ilearn.content.feign;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 搜索服务降级
 * @date 2/23/2023 11:11 AM
 */
@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable cause) {
        return courseIndex -> {
            // 降级方法
            cause.printStackTrace();
            log.error("远程调用添加课程索引失败, 熔断异常: {}", cause.getMessage());
            return Boolean.FALSE;
        };
    }
}
