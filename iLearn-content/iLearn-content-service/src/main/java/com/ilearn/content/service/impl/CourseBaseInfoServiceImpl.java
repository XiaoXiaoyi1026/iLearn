package com.ilearn.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ilearn.base.dictionary.CourseType;
import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.model.PageParams;
import com.ilearn.base.model.PageResult;
import com.ilearn.base.dictionary.CourseAuditStatus;
import com.ilearn.base.dictionary.CourseReleaseStatus;
import com.ilearn.content.mapper.CourseBaseMapper;
import com.ilearn.content.mapper.CourseCategoryMapper;
import com.ilearn.content.mapper.CourseMarketMapper;
import com.ilearn.content.model.dto.AddCourseDto;
import com.ilearn.content.model.dto.CourseBaseInfoDto;
import com.ilearn.content.model.dto.QueryCourseParamsDto;
import com.ilearn.content.model.dto.UpdateCourseDto;
import com.ilearn.content.model.po.CourseBase;
import com.ilearn.content.model.po.CourseCategory;
import com.ilearn.content.model.po.CourseMarket;
import com.ilearn.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Autowired
    private CourseMarketServiceImpl courseMarketService;

    @Override
    public PageResult<CourseBase> queryPageList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
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

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public CourseBaseInfoDto add(Long companyId, AddCourseDto addCourseDto) {
        // 对数据进行封装, 封装成持久层mapper需要的对象
        CourseBase courseBase = new CourseBase();
        // 将dto对象放入po类中, 属性一致
        BeanUtils.copyProperties(addCourseDto, courseBase);
        // 设置机构id
        courseBase.setCompanyId(companyId);
        // 设置创建时间, 默认为当前时间
        LocalDateTime now = LocalDateTime.now();
        courseBase.setCreateDate(now);
        // 设置更新时间, 默认为当前时间
        courseBase.setChangeDate(now);
        // 设置课程审核状态, 默认为未提交
        courseBase.setAuditStatus(CourseAuditStatus.UNSUBMITTED);
        // 设置课程发布状态, 默认为未发布
        courseBase.setStatus(CourseReleaseStatus.UNPUBLISHED);
        // 封装营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(addCourseDto, courseMarket);

        // 调用mapper进行持久化
        if (courseBaseMapper.insert(courseBase) < 1) {
            /*throw new RuntimeException("插入课程失败!");*/
            ILearnException.cast("添加课程失败!");
        }

        // 为防止事务失效, 用自身的指针去调用非事务方法
        this.saveOrUpdateCourseMarket(
                courseMarket,
                courseBase.getId(),
                addCourseDto.getCharge(),
                addCourseDto.getPrice()
        );

        // 拼装响应结果
        return this.buildDtoInfo(courseBase, courseMarket);
    }

    @Override
    public CourseBaseInfoDto buildDtoInfo(Long courseId) {
        // 获取课程基本信息
        CourseBase baseInfo = courseBaseMapper.selectById(courseId);
        if (baseInfo == null) {
            ILearnException.cast("课程不存在!");
        }
        // 获取课程营销信息
        CourseMarket marketInfo = courseMarketMapper.selectById(courseId);
        if (marketInfo == null) {
            marketInfo = new CourseMarket();
        }
        // 创建CourseBaseInfoDto对象并封装
        return this.buildDtoInfo(baseInfo, marketInfo);
    }

    private @NotNull CourseBaseInfoDto buildDtoInfo(CourseBase baseInfo, CourseMarket marketInfo) {
        // 创建CourseBaseInfoDto对象并封装
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(baseInfo, courseBaseInfoDto);
        BeanUtils.copyProperties(marketInfo, courseBaseInfoDto);
        // 根据课程的mt和st获取对应的name并封装
        CourseCategory mtCategory = courseCategoryMapper.selectById(baseInfo.getMt());
        if (mtCategory != null) {
            courseBaseInfoDto.setMtName(mtCategory.getName());
        }
        CourseCategory stCategory = courseCategoryMapper.selectById(baseInfo.getSt());
        if (stCategory != null) {
            courseBaseInfoDto.setStName(stCategory.getName());
        }
        return courseBaseInfoDto;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public CourseBaseInfoDto update(Long companyId, UpdateCourseDto updateCourseDto) {

        Long courseId = updateCourseDto.getId();

        // 校验(Controller层做, Service层只负责业务流程的合法性校验)
        CourseBase courseBase = courseBaseMapper.selectById(courseId);

        if (courseBase == null) {
            // 如果该课程不存在抛出异常
            ILearnException.cast("课程不存在!");
        }

        // null safe处理, 判断更改的课程和机构id是否匹配
        if (!Objects.equals(companyId, courseBase.getCompanyId())) {
            ILearnException.cast("不可修改不是自己机构的课程!");
        }

        // 将dto对象放入po类中, 属性一致
        BeanUtils.copyProperties(updateCourseDto, courseBase);

        // 设置更新时间
        courseBase.setChangeDate(LocalDateTime.now());

        // 调用mapper进行持久化
        if (courseBaseMapper.updateById(courseBase) < 1) {
            ILearnException.cast("更新课程失败!");
        }

        // 营销信息有则更新, 没有则添加
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        if (courseMarket == null) {
            courseMarket = new CourseMarket();
        }
        BeanUtils.copyProperties(updateCourseDto, courseMarket);

        // 为防止事务失效, 可以在本类中注入自身, 然后用自身的指针去调用方法
        this.saveOrUpdateCourseMarket(
                courseMarket,
                courseId,
                updateCourseDto.getCharge(),
                updateCourseDto.getPrice()
        );

        // 拼装响应结果
        return this.buildDtoInfo(courseBase, courseMarket);
    }

    private void saveOrUpdateCourseMarket(@NotNull CourseMarket courseMarket, Long courseId, String courseType, Float price) {
        courseMarket.setId(courseId);

        // 如果课程收费, 则价格必须输入
        if (CourseType.CHARGE.equals(courseType)) {
            if (price == null) {
                ILearnException.cast("收费课程必须输入价格!");
            } else if (price <= 0) {
                ILearnException.cast("课程价格必须大于0!");
            }
        }

        if (!courseMarketService.saveOrUpdate(courseMarket)) {
            ILearnException.cast("更新课程营销信息失败!");
        }
    }

}
