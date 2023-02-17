package com.ilearn.content.service;

import com.ilearn.content.model.dto.CoursePreviewDto;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程预览服务
 * @date 2/15/2023 5:06 PM
 */
public interface CoursePublishService {

    /**
     * 根据课程id获取课程的预览信息
     *
     * @param courseId 课程id
     * @return 课程基本信息, 营销信息和教学计划信息
     */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * 课程提交审核
     *
     * @param companyId 课程所属的机构id
     * @param courseId  课程id
     */
    void commitAudit(Long companyId, Long courseId);

}
