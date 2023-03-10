package com.ilearn.learning.feign;

import com.ilearn.content.model.po.CoursePublish;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 内容管理服务远程调用
 * @date 3/9/2023 2:22 PM
 */
@FeignClient(name = "iLearn-content-api", path = "/content", fallbackFactory = ContentServiceClientFallbackFactory.class)
public interface ContentServiceClient {

    @ApiOperation(value = "获取课程发布信息")
    @GetMapping("/r/coursepublish/{courseId}")
    @ResponseBody
    CoursePublish getCoursePublishInfo(@PathVariable(name = "courseId") Long courseId);

}
