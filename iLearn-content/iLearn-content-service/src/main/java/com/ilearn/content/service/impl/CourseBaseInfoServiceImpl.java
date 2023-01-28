package com.ilearn.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ilearn.base.exception.CommonError;
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
import com.ilearn.content.model.po.CourseBase;
import com.ilearn.content.model.po.CourseCategory;
import com.ilearn.content.model.po.CourseMarket;
import com.ilearn.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public CourseBaseInfoDto addCourse(Long companyId, AddCourseDto addCourseDto) {
        // 对参数进行合法性校验
        if (StringUtils.isBlank(addCourseDto.getName())) {
            /*throw new RuntimeException("课程名称为空");*/
            ILearnException.cast("课程名称为空");
            /*ILearnException.cast(CommonError.PARAMS_ERROR);*/
        }

        if (StringUtils.isBlank(addCourseDto.getMt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(addCourseDto.getSt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(addCourseDto.getGrade())) {
            throw new RuntimeException("课程等级为空");
        }

        if (StringUtils.isBlank(addCourseDto.getTeachmode())) {
            throw new RuntimeException("教育模式为空");
        }

        if (StringUtils.isBlank(addCourseDto.getUsers())) {
            throw new RuntimeException("适应人群为空");
        }

        if (StringUtils.isBlank(addCourseDto.getCharge())) {
            throw new RuntimeException("收费规则为空");
        }
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
            throw new RuntimeException("插入课程失败!");
        }
        Long courseId = courseBase.getId();
        courseMarket.setId(courseId);
        // 如果课程收费, 则价格必须输入
        if ("201001".equals(addCourseDto.getCharge())) {
            Float price = addCourseDto.getPrice();
            if (price == null) {
                throw new RuntimeException("收费课程必须输入价格!");
            } else if (price <= 0) {
                throw new RuntimeException("价格必须大于0!");
            }
        }
        if (courseMarketMapper.insert(courseMarket) < 1) {
            throw new RuntimeException("插入营销信息失败!");
        }

        // 拼装响应结果
        return buildCourseInfo(courseId);
    }

    /**
     * 根据课程id封装课程的基本信息和营销信息
     *
     * @param courseId 课程id
     * @return 课程的基本信息和营销信息
     */
    private CourseBaseInfoDto buildCourseInfo(Long courseId) {
        // 获取课程基本信息
        CourseBase baseInfo = courseBaseMapper.selectById(courseId);

        // 获取课程营销信息
        CourseMarket marketInfo = courseMarketMapper.selectById(courseId);

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

}
