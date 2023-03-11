package com.ilearn.learning.api;

import com.ilearn.content.model.po.User;
import com.ilearn.learning.model.dto.ChooseCourseDto;
import com.ilearn.learning.model.dto.CourseTablesDto;
import com.ilearn.learning.service.CourseTablesService;
import com.ilearn.learning.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程表接口
 * @date 3/10/2023 1:33 PM
 */
@Api(value = "课程表相关接口")
@RestController
@Slf4j
public class CourseTablesController {

    private CourseTablesService courseTablesService;

    @Autowired
    void setCourseTablesService(CourseTablesService courseTablesService) {
        this.courseTablesService = courseTablesService;
    }

    @ApiOperation(value = "添加选课记录")
    @PostMapping("/choosecourse/{courseId}")
    public ChooseCourseDto addChooseCourse(@PathVariable(name = "courseId") Long courseId) {
        User securityInfo = SecurityUtil.getSecurityInfo();
        String userId = securityInfo.getId();
        return courseTablesService.addChooseCourseDto(userId, courseId);
    }

    @ApiOperation(value = "查询学习资格")
    @PostMapping("/choosecourse/learnstatus/{courseId}")
    public CourseTablesDto getLearningQualification(@PathVariable(name = "courseId") Long courseId) {
        User securityInfo = SecurityUtil.getSecurityInfo();
        String userId = securityInfo.getId();
        return courseTablesService.getLearningQualification(userId, courseId);
    }

}
