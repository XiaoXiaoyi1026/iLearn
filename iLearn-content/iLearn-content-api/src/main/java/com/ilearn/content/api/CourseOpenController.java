package com.ilearn.content.api;

import com.ilearn.content.model.dto.CoursePreviewDto;
import com.ilearn.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程开放接口, 可以在任何地方进行调用
 * @date 2/17/2023 12:51 PM
 */
@Api(value = "课程开放接口")
@RestController
@RequestMapping("/open")
public class CourseOpenController {

    private CoursePublishService coursePreviewService;

    @Autowired
    void setCoursePublishService(CoursePublishService coursePreviewService) {
        this.coursePreviewService = coursePreviewService;
    }

    /**
     * 根据课程id查询课程预览信息
     *
     * @param courseId 课程id
     * @return 课程预览信息
     */
    @GetMapping("/course/whole/{courseId}")
    @ApiOperation(value = "查询课程预览信息")
    public CoursePreviewDto getCoursePreviewInfo(@PathVariable("courseId") Long courseId) {
        return coursePreviewService.getCoursePreviewInfo(courseId);
    }

}
