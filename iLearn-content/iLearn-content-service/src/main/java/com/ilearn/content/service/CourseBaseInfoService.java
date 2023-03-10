package com.ilearn.content.service;

import com.ilearn.base.model.PageRequestParams;
import com.ilearn.base.model.PageResponse;
import com.ilearn.content.model.dto.AddCourseDto;
import com.ilearn.content.model.dto.CourseBaseInfoDto;
import com.ilearn.content.model.dto.QueryCourseParamsDto;
import com.ilearn.content.model.dto.UpdateCourseDto;
import com.ilearn.content.model.po.CourseBase;
import com.ilearn.content.model.po.CourseMarket;

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
     * @param pageRequestParams    分页参数
     * @param queryCourseParamsDto 课程查询条件
     * @return 分页课程信息
     */
    PageResponse<CourseBase> queryPageList(PageRequestParams pageRequestParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * 课程基本信息分页查询
     *
     * @param companyId            机构id
     * @param pageRequestParams    分页参数
     * @param queryCourseParamsDto 课程查询条件
     * @return 分页课程信息
     */
    PageResponse<CourseBase> queryPageList(String companyId, PageRequestParams pageRequestParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * 新增课程
     *
     * @param companyId    企业id
     * @param addCourseDto 课程信息
     * @return 课程基本信息Dto, 包含基本信息和营销信息
     */
    CourseBaseInfoDto add(Long companyId, AddCourseDto addCourseDto);

    /**
     * 根据课程id封装课程的基本信息和营销信息
     *
     * @param courseId 课程id
     * @return 课程的基本信息和营销信息
     */
    CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    /**
     * 更新课程信息
     *
     * @param companyId       机构id, 校验机构修改的课程是否是自己机构所属的
     * @param updateCourseDto 更新课程的Dto
     * @return 更新后的课程Dto
     */
    CourseBaseInfoDto update(Long companyId, UpdateCourseDto updateCourseDto);

    /**
     * 保存课程营销信息
     *
     * @param courseMarket 营销信息
     * @param courseId     课程id
     * @param courseType   课程类型
     * @param price        课程价格
     */
    void saveCourseMarketInfo(CourseMarket courseMarket, Long courseId, String courseType, Float price);
}
