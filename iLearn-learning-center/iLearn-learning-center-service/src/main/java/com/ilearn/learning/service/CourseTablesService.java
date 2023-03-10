package com.ilearn.learning.service;

import com.ilearn.learning.model.dto.ChooseCourseDto;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程表服务
 * @date 3/9/2023 4:28 PM
 */
public interface CourseTablesService {

    /**
     * 添加选课记录
     *
     * @param userId   用户id
     * @param courseId 课程id
     * @return 选课Dto
     */
    ChooseCourseDto addChooseCourseDto(String userId, Long courseId);
}
