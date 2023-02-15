package com.ilearn.content.api;

import com.ilearn.base.exception.ValidationGroups;
import com.ilearn.base.model.PageRequestParams;
import com.ilearn.base.model.PageResponse;
import com.ilearn.content.model.dto.AddCourseDto;
import com.ilearn.content.model.dto.CourseBaseInfoDto;
import com.ilearn.content.model.dto.QueryCourseParamsDto;
import com.ilearn.content.model.dto.UpdateCourseDto;
import com.ilearn.content.model.po.CourseBase;
import com.ilearn.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程基本信息相关接口
 * @date 1/24/2023 5:03 PM
 */

@RestController
@Api(value = "课程管理", tags = "课程管理相关接口")
@RequestMapping("/course")
public class CourseBaseInfoController {

    private final CourseBaseInfoService courseBaseInfoService;

    @Autowired
    private CourseBaseInfoController(CourseBaseInfoService courseBaseInfoService) {
        this.courseBaseInfoService = courseBaseInfoService;
    }

    @PostMapping("/list")
    @ApiOperation("课程分页查询")
    public PageResponse<CourseBase> list(PageRequestParams pageRequestParams, @RequestBody QueryCourseParamsDto queryCourseParamsDto) {
        // Controller -> Service -> Mapper(dao)
        return courseBaseInfoService.queryPageList(pageRequestParams, queryCourseParamsDto);
    }

    @PostMapping
    @ApiOperation("新增课程")
    public CourseBaseInfoDto add(@RequestBody @Validated(value = {ValidationGroups.Insert.class}) AddCourseDto addCourseDto) {
        // 认证/授权后, 可获取登录用户和所属培训机构的id
        Long companyId = 1026L;
        return courseBaseInfoService.add(companyId, addCourseDto);
    }

    @GetMapping("/{courseId}")
    @ApiOperation("根据课程id获取对应的课程信息")
    public CourseBaseInfoDto getById(@PathVariable(name = "courseId") Long courseId) {
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    @PutMapping
    @ApiOperation("更新课程信息")
    public CourseBaseInfoDto update(@RequestBody @Validated(value = {ValidationGroups.Update.class}) UpdateCourseDto updateCourseDto) {
        Long companyId = 1026L;
        return courseBaseInfoService.update(companyId, updateCourseDto);
    }

}
