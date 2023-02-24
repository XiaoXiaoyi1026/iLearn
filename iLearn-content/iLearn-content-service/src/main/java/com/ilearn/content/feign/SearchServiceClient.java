package com.ilearn.content.feign;

import com.ilearn.content.feign.model.CourseIndex;
import io.swagger.annotations.ApiOperation;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 搜索服务远程调用
 * @date 2/23/2023 11:07 AM
 */
@FeignClient(name = "iLearn-search", url = "localhost:63080", path = "/search", fallbackFactory = SearchServiceClientFallbackFactory.class)
public interface SearchServiceClient {

    @ApiOperation("添加课程索引")
    @PostMapping("/index/course")
    Boolean add(@RequestBody @NotNull CourseIndex courseIndex);

}
