package com.ilearn.learning.service;

import com.ilearn.content.model.po.CoursePublish;
import com.ilearn.learning.model.dto.ChooseCourseDto;
import com.ilearn.learning.model.dto.CourseTablesDto;
import com.ilearn.learning.model.po.ChooseCourse;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

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

    /**
     * 查询学习资格
     *
     * @param userId   用户id
     * @param courseId 课程id
     * @return 学习资格
     */
    CourseTablesDto getLearningQualification(String userId, Long courseId);

    /**
     * 添加选课记录(插入表)
     *
     * @param userId            用户id
     * @param now               现在的时间
     * @param coursePublishInfo 课程发布信息
     * @return 选课Dto
     */
    ChooseCourse addChooseCourse(String userId, LocalDateTime now, @NotNull CoursePublish coursePublishInfo);

    /**
     * 添加课程到课程表
     *
     * @param now          现在的时间
     * @param chooseCourse 选课信息
     */
    void addCourseTables(LocalDateTime now, ChooseCourse chooseCourse);
}
