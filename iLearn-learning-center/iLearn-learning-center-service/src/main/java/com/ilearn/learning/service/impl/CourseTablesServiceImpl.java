package com.ilearn.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.mapper.CourseLearningQualification;
import com.ilearn.base.mapper.CourseSelectionStatus;
import com.ilearn.base.mapper.CourseSelectionType;
import com.ilearn.base.mapper.CourseType;
import com.ilearn.content.model.po.CoursePublish;
import com.ilearn.learning.feign.ContentServiceClient;
import com.ilearn.learning.mapper.ChooseCourseMapper;
import com.ilearn.learning.mapper.CourseTablesMapper;
import com.ilearn.learning.model.dto.ChooseCourseDto;
import com.ilearn.learning.model.po.ChooseCourse;
import com.ilearn.learning.model.po.CourseTables;
import com.ilearn.learning.service.CourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程表实现类
 * @date 3/9/2023 4:34 PM
 */
@Slf4j
@Service
public class CourseTablesServiceImpl implements CourseTablesService {

    private ChooseCourseMapper chooseCourseMapper;

    private ContentServiceClient contentServiceClient;

    private CourseTablesMapper courseTablesMapper;

    private CourseTablesServiceImpl courseTablesService;

    @Autowired
    void setChooseCourseMapper(ChooseCourseMapper chooseCourseMapper) {
        this.chooseCourseMapper = chooseCourseMapper;
    }

    @Autowired
    void setContentServiceClient(ContentServiceClient contentServiceClient) {
        this.contentServiceClient = contentServiceClient;
    }

    @Autowired
    void setCourseTablesMapper(CourseTablesMapper courseTablesMapper) {
        this.courseTablesMapper = courseTablesMapper;
    }

    @Autowired
    void setCourseTablesService(CourseTablesServiceImpl courseTablesService) {
        this.courseTablesService = courseTablesService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChooseCourseDto addChooseCourseDto(String userId, Long courseId) {
        LocalDateTime now = LocalDateTime.now();
        // feign远程调用查询课程发布信息
        CoursePublish coursePublishInfo = contentServiceClient.getCoursePublishInfo(courseId);
        ChooseCourse chooseCourse;
        if (CourseType.FREE.equals(coursePublishInfo.getCharge())) {
            // 如果课程类型为免费, 则查询免费的选课记录
            chooseCourse = getChooseFreeCourse(userId, courseId);
        } else {
            // 否则查询收费的选课记录
            chooseCourse = getChoosePaidCourse(userId, courseId);
        }
        if (chooseCourse == null) {
            // 如果选课记录为空则新增
            chooseCourse = addChooseCourse(userId, now, coursePublishInfo);
            // 新增选课记录完成后如果选课状态为成功且课程表中没有该课程, 则新增该课程
            if (CourseSelectionStatus.SUCCESSFUL.equals(chooseCourse.getStatus()) && getCourseTables(userId, courseId) == null) {
                courseTablesService.addCourseTables(now, chooseCourse);
            }
        }
        // 封装返回数据
        ChooseCourseDto chooseCourseDto = new ChooseCourseDto();
        BeanUtils.copyProperties(chooseCourse, chooseCourseDto);
        // 判断课程学习资格
        String courseLearningQualification = CourseLearningQualification.NORMAL;
        if (CourseSelectionStatus.TO_BE_PAID.equals(chooseCourse.getStatus())) {
            courseLearningQualification = CourseLearningQualification.EXCEPTION;
        } else if (chooseCourse.getValidtimeEnd().isBefore(now)) {
            courseLearningQualification = CourseLearningQualification.EXPIRED;
        }
        chooseCourseDto.setLearningStatus(courseLearningQualification);
        return chooseCourseDto;
    }

    private void addCourseTables(LocalDateTime now, ChooseCourse chooseCourse) {
        CourseTables courseTables = new CourseTables();
        BeanUtils.copyProperties(chooseCourse, courseTables);
        // 选课表主键
        courseTables.setChooseCourseId(chooseCourse.getId());
        // 选课类型
        courseTables.setCourseType(chooseCourse.getOrderType());
        courseTables.setUpdateDate(now);
        if (courseTablesMapper.insert(courseTables) == 0) {
            log.error("添加到课程表失败: {}", courseTables);
            ILearnException.cast("添加到课程表失败");
        }
    }

    @NotNull
    private ChooseCourse addChooseCourse(String userId, LocalDateTime now, @NotNull CoursePublish coursePublishInfo) {
        ChooseCourse chooseCourse = new ChooseCourse();
        chooseCourse.setCourseId(coursePublishInfo.getId());
        chooseCourse.setCourseName(coursePublishInfo.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursePublishInfo.getCompanyId());
        chooseCourse.setOrderType(
                CourseType.FREE.equals(coursePublishInfo.getCharge()) ?
                        CourseSelectionType.FREE : CourseSelectionType.CHARGE
        );
        chooseCourse.setCreateDate(now);
        chooseCourse.setCoursePrice(coursePublishInfo.getPrice());
        Integer validDays = coursePublishInfo.getValidDays();
        chooseCourse.setValidDays(validDays);
        chooseCourse.setStatus(
                CourseSelectionType.FREE.equals(chooseCourse.getOrderType()) ?
                        CourseSelectionStatus.SUCCESSFUL : CourseSelectionStatus.TO_BE_PAID
        );
        chooseCourse.setValidtimeStart(now);
        chooseCourse.setValidtimeEnd(now.plusDays(validDays));
        chooseCourse.setRemarks(coursePublishInfo.getRemark());
        if (chooseCourseMapper.insert(chooseCourse) == 0) {
            log.error("添加选课记录失败: {}", chooseCourse);
            ILearnException.cast("添加选课记录失败");
        }
        return chooseCourse;
    }

    private @Nullable ChooseCourse getChooseFreeCourse(String userId, Long courseId) {
        // 查询是否已经添加过该课程
        LambdaQueryWrapper<ChooseCourse> chooseCourseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chooseCourseLambdaQueryWrapper.eq(ChooseCourse::getUserId, userId)
                .eq(ChooseCourse::getCourseId, courseId)
                .eq(ChooseCourse::getOrderType, CourseSelectionType.FREE)
                .eq(ChooseCourse::getStatus, CourseSelectionStatus.SUCCESSFUL);
        // 因为不存在唯一约束, 所以不能用selectOne
        List<ChooseCourse> chooseCourses = chooseCourseMapper.selectList(chooseCourseLambdaQueryWrapper);
        if (chooseCourses != null && chooseCourses.size() > 0) {
            return chooseCourses.get(0);
        }
        return null;
    }

    private @Nullable ChooseCourse getChoosePaidCourse(String userId, Long courseId) {
        // 查询是否已经添加过该课程
        LambdaQueryWrapper<ChooseCourse> chooseCourseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chooseCourseLambdaQueryWrapper.eq(ChooseCourse::getUserId, userId)
                .eq(ChooseCourse::getCourseId, courseId)
                .eq(ChooseCourse::getOrderType, CourseSelectionType.CHARGE)
                .eq(ChooseCourse::getStatus, CourseSelectionStatus.TO_BE_PAID);
        // 因为不存在唯一约束, 所以不能用selectOne
        List<ChooseCourse> chooseCourses = chooseCourseMapper.selectList(chooseCourseLambdaQueryWrapper);
        if (chooseCourses != null && chooseCourses.size() > 0) {
            return chooseCourses.get(0);
        }
        return null;
    }

    private @Nullable CourseTables getCourseTables(String userId, Long courseId) {
        // 由于course_ables表中添加了由userId和courseId共同决定的唯一索引, 所以可以用selectOne
        LambdaQueryWrapper<CourseTables> courseTablesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        courseTablesLambdaQueryWrapper.eq(CourseTables::getUserId, userId)
                .eq(CourseTables::getCourseId, courseId);
        return courseTablesMapper.selectOne(courseTablesLambdaQueryWrapper);
    }
}
