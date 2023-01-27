package com.ilearn.content.api;

import com.ilearn.base.model.PageParams;
import com.ilearn.base.model.PageResult;
import com.ilearn.content.model.dto.AddCourseDto;
import com.ilearn.content.model.dto.CourseBaseInfoDto;
import com.ilearn.content.model.dto.QueryCourseParamsDto;
import com.ilearn.content.model.po.CourseBase;
import com.ilearn.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程基本信息相关接口
 * @date 1/24/2023 5:03 PM
 */

@RestController
@Api(value = "课程管理", tags = "课程管理相关接口")
public class CourseBaseInfoController {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @GetMapping("/course/list")
    @ApiOperation("课程分页查询")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParamsDto) {
        // Controller -> Service -> Mapper(dao)
        return courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
    }

    @ApiOperation("新增课程")
    @PostMapping("/course")
    public CourseBaseInfoDto add(@RequestBody AddCourseDto addCourseDto) {
        return null;
    }

}
