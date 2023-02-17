package com.ilearn.content.service.impl;

import com.ilearn.base.dictionary.CourseAuditStatus;
import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.utils.StringUtil;
import com.ilearn.content.model.dto.CourseBaseInfoDto;
import com.ilearn.content.model.dto.CoursePreviewDto;
import com.ilearn.content.model.dto.TeachPlanDto;
import com.ilearn.content.service.CourseBaseInfoService;
import com.ilearn.content.service.CoursePublishService;
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
public class CoursePublishServiceImpl implements CoursePublishService {

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

    @Override
    public void commitAudit(Long companyId, Long courseId) {
        // 获取课程基本信息, 营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        if (courseBaseInfo == null) {
            log.error("提交审核失败, 课程不存在, courseId: {}", courseId);
            ILearnException.cast("提交审核失败, 课程不存在");
        }
        // 校验1: 课程必须属于当前机构
        if (!courseBaseInfo.getCompanyId().equals(companyId)) {
            log.error("提交审核失败, 课程不属于当前机构, courseId: {}", courseId);
            ILearnException.cast("不能提交不属于自己机构的课程");
        }
        // 校验2: 当审核状态为已提交, 则不能再次提交
        if (CourseAuditStatus.SUBMITTED.equals(courseBaseInfo.getAuditStatus())) {
            log.error("提交审核失败, 当前审核状态为已提交, courseId: {}", courseId);
            ILearnException.cast("当前审核状态为已提交");
        }
        // 校验3: 课程图片必须指定
        if (StringUtil.isEmpty(courseBaseInfo.getPic())) {
            log.error("提交审核失败, 课程图片未指定, courseId: {}", courseId);
            ILearnException.cast("课程图片未指定");
        }
        // 校验4: 课程计划必须添加
        List<TeachPlanDto> courseTeachPlans = teachPlanService.getCourseTeachPlans(courseId);
        if (courseTeachPlans == null || courseTeachPlans.isEmpty()) {
            log.error("提交审核失败, 课程计划未添加, courseId: {}", courseId);
            ILearnException.cast("课程计划未添加");
        }
        // 校验都通过, 可以开始准备插入预发布表

    }
}
