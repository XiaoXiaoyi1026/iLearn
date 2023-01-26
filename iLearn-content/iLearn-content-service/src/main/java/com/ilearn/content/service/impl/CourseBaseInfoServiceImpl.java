package com.ilearn.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ilearn.base.model.PageParams;
import com.ilearn.base.model.PageResult;
import com.ilearn.content.mapper.CourseBaseMapper;
import com.ilearn.content.model.dto.QueryCourseParamsDto;
import com.ilearn.content.model.po.CourseBase;
import com.ilearn.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 实现类
 * @date 1/26/2023 1:49 PM
 */
@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        // 拼接查询条件
        LambdaQueryWrapper<CourseBase> courseBaseLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 根据课程名称
        courseBaseLambdaQueryWrapper.like(
                StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),
                CourseBase::getName,
                queryCourseParamsDto.getCourseName()

        );

        // 根据课程的审核状态
        courseBaseLambdaQueryWrapper.eq(
                StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),
                CourseBase::getAuditStatus,
                queryCourseParamsDto.getAuditStatus()
        );

        // 根据课程的发布状态
        courseBaseLambdaQueryWrapper.eq(
                StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),
                CourseBase::getStatus,
                queryCourseParamsDto.getPublishStatus()
        );

        // 准备分页参数
        long pageNo = pageParams.getPageNo();
        long pageSize = pageParams.getPageSize();
        Page<CourseBase> page = new Page<>(pageNo, pageSize);

        // 分页查询 (E page: 分页参数, @Param("ew") Wrapper<T> queryWrapper: 查询条件)
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, courseBaseLambdaQueryWrapper);

        // 准备响应数据
        PageResult<CourseBase> pageResult = new PageResult<>();
        pageResult.setItems(courseBasePage.getRecords());
        pageResult.setPageSize(pageSize);
        pageResult.setPage(pageNo);
        pageResult.setCounts(courseBasePage.getTotal());

        return pageResult;
    }
}
