package com.ilearn.content.api;

import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.exception.ValidationGroups;
import com.ilearn.base.model.PageRequestParams;
import com.ilearn.base.model.PageResponse;
import com.ilearn.content.model.dto.AddCourseDto;
import com.ilearn.content.model.dto.CourseBaseInfoDto;
import com.ilearn.content.model.dto.QueryCourseParamsDto;
import com.ilearn.content.model.dto.UpdateCourseDto;
import com.ilearn.content.model.po.CourseBase;
import com.ilearn.content.model.po.IlearnUser;
import com.ilearn.content.service.CourseBaseInfoService;
import com.ilearn.content.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程基本信息相关接口
 * @date 1/24/2023 5:03 PM
 */
@Slf4j
@RestController
@Api(value = "课程管理", tags = "课程管理相关接口")
@RequestMapping("/course")
public class CourseBaseInfoController {

    private CourseBaseInfoService courseBaseInfoService;

    @Autowired
    void setCourseBaseInfoService(CourseBaseInfoService courseBaseInfoService) {
        this.courseBaseInfoService = courseBaseInfoService;
    }

    /**
     * 课程分页查询
     *
     * @param pageRequestParams    分页参数
     * @param queryCourseParamsDto 课程信息
     * @return 查询结果
     */
    @PostMapping("/list")
    @ApiOperation("课程分页查询")
    @PreAuthorize("hasAuthority('course_find_list')")
    public PageResponse<CourseBase> list(PageRequestParams pageRequestParams, @RequestBody QueryCourseParamsDto queryCourseParamsDto) {
        // 获取登录用户的授权信息
        IlearnUser userInfo = SecurityUtil.getInfoFromSecurityContext(IlearnUser.class);
        if (userInfo == null) {
            ILearnException.cast("授权信息异常, 请重新登录后再试");
        }
        String companyId = userInfo.getCompanyId();
        // Controller -> Service -> Mapper(dao)
        return courseBaseInfoService.queryPageList(companyId, pageRequestParams, queryCourseParamsDto);
    }

    /**
     * 新增课程
     *
     * @param addCourseDto 新增的课程信息
     * @return 新增的课程信息
     */
    @PostMapping
    @ApiOperation("新增课程")
    public CourseBaseInfoDto add(@RequestBody @Validated(value = {ValidationGroups.Insert.class}) AddCourseDto addCourseDto) {
        // 认证/授权后, 可获取登录用户和所属培训机构的id
        Long companyId = 1026L;
        return courseBaseInfoService.add(companyId, addCourseDto);
    }

    /**
     * 根据课程id获取对应的课程信息
     *
     * @param courseId 课程id
     * @return 课程基本信息和营销信息
     */
    @GetMapping("/{courseId}")
    @ApiOperation("根据课程id获取对应的课程信息")
    public CourseBaseInfoDto getById(@PathVariable(name = "courseId") Long courseId) {
        // 使用工具类拿用户认证信息
        IlearnUser ilearnUser = SecurityUtil.getInfoFromSecurityContext(IlearnUser.class);
        if (ilearnUser == null) {
            log.error("认证失败, 非法访问");
            ILearnException.cast("获取认证信息失败, 请确认登录信息");
        }
        log.info("查询课程信息, 认证信息: {}", ilearnUser);
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    /**
     * 更新课程信息
     *
     * @param updateCourseDto 更新信息
     * @return 更新结果
     */
    @PutMapping
    @ApiOperation("更新课程信息")
    public CourseBaseInfoDto update(@RequestBody @Validated(value = {ValidationGroups.Update.class}) UpdateCourseDto updateCourseDto) {
        Long companyId = 1026L;
        return courseBaseInfoService.update(companyId, updateCourseDto);
    }

}
