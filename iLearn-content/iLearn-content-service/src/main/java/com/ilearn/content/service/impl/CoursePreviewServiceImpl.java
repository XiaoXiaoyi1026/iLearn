package com.ilearn.content.service.impl;

import com.ilearn.base.exception.ILearnException;
import com.ilearn.content.model.dto.CourseBaseInfoDto;
import com.ilearn.content.model.dto.CoursePreviewDto;
import com.ilearn.content.model.dto.TeachPlanDto;
import com.ilearn.content.service.CourseBaseInfoService;
import com.ilearn.content.service.CoursePreviewService;
import com.ilearn.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程预览服务实现类
 * @date 2/15/2023 5:08 PM
 */
@Slf4j
@Service
public class CoursePreviewServiceImpl implements CoursePreviewService {

    private CourseBaseInfoService courseBaseInfoService;

    private TeachPlanService teachPlanService;

    @Autowired
    void setCourseBaseInfoService(CourseBaseInfoServiceImpl courseBaseInfoService) {
        this.courseBaseInfoService = courseBaseInfoService;
    }

    @Autowired
    void setTeachPlanService(TeachPlanService teachPlanService) {
        this.teachPlanService = teachPlanService;
    }

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        // 获取课程基本信息和营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        if (courseBaseInfo == null) {
            log.error("查询课程基本信息失败, courseId: {}", courseId);
            ILearnException.cast("查询课程基本信息失败");
        }
        // 获取课程的教学计划
        List<TeachPlanDto> courseTeachPlans = teachPlanService.getCourseTeachPlans(courseId);
        if (courseTeachPlans == null) {
            log.error("查询课程教学计划信息失败, courseId: {}", courseId);
            ILearnException.cast("查询课程教学计划失败");
        }
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBaseInfoDto(courseBaseInfo);
        coursePreviewDto.setTeachPlanDtoList(courseTeachPlans);
        return coursePreviewDto;
    }
}
