package com.ilearn.content.service;

import com.ilearn.base.model.PageParams;
import com.ilearn.base.model.PageResult;
import com.ilearn.content.model.dto.AddCourseDto;
import com.ilearn.content.model.dto.CourseBaseInfoDto;
import com.ilearn.content.model.dto.QueryCourseParamsDto;
import com.ilearn.content.model.po.CourseBase;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程管理service
 * @date 1/26/2023 1:44 PM
 */
public interface CourseBaseInfoService {

    /**
     * 课程基本信息分页查询
     *
     * @param pageParams           分页参数
     * @param queryCourseParamsDto 课程查询条件
     * @return 分页课程信息
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * 新增课程
     *
     * @param companyId    企业id
     * @param addCourseDto 课程信息
     * @return 课程基本信息Dto, 包含基本信息和营销信息
     */
    CourseBaseInfoDto addCourse(Long companyId, AddCourseDto addCourseDto);
}
