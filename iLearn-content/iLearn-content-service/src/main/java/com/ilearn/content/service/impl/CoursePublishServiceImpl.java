package com.ilearn.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ilearn.base.mapper.CourseAuditStatus;
import com.ilearn.base.mapper.CoursePublishStatus;
import com.ilearn.base.mapper.TaskType;
import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.model.ResponseMessage;
import com.ilearn.base.utils.JsonUtil;
import com.ilearn.base.utils.StringUtil;
import com.ilearn.content.mapper.CourseBaseMapper;
import com.ilearn.content.mapper.CoursePublishMapper;
import com.ilearn.content.mapper.CoursePublishPreMapper;
import com.ilearn.content.mapper.CourseTeacherMapper;
import com.ilearn.content.model.dto.CourseBaseInfoDto;
import com.ilearn.content.model.dto.CoursePreviewDto;
import com.ilearn.content.model.dto.TeachPlanDto;
import com.ilearn.content.model.po.*;
import com.ilearn.content.service.CourseBaseInfoService;
import com.ilearn.content.service.CoursePublishService;
import com.ilearn.content.service.TeachPlanService;
import com.ilearn.content.service.asserts.CourseAssert;
import com.ilearn.task.model.po.MqMessage;
import com.ilearn.task.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    private CourseBaseMapper courseBaseMapper;

    private TeachPlanService teachPlanService;

    private CourseTeacherMapper courseTeacherMapper;

    private CoursePublishPreMapper coursePublishPreMapper;

    private CoursePublishMapper coursePublishMapper;

    private CoursePublishService coursePublishService;

    private MqMessageService mqMessageService;

    /**
     * 发布课程的模板文件
     */
    private static final String COURSE_PUBLISH_TEMPLATE_FILE = "course_template.ftl";

    @Autowired
    void setCourseBaseInfoService(CourseBaseInfoService courseBaseInfoService) {
        this.courseBaseInfoService = courseBaseInfoService;
    }

    @Autowired
    void setCourseBaseMapper(CourseBaseMapper courseBaseMapper) {
        this.courseBaseMapper = courseBaseMapper;
    }

    @Autowired
    void setTeachPlanService(TeachPlanService teachPlanService) {
        this.teachPlanService = teachPlanService;
    }

    @Autowired
    void setCourseTeacherMapper(CourseTeacherMapper courseTeacherMapper) {
        this.courseTeacherMapper = courseTeacherMapper;
    }

    @Autowired
    void setCoursePublishPreMapper(CoursePublishPreMapper coursePublishPreMapper) {
        this.coursePublishPreMapper = coursePublishPreMapper;
    }

    @Autowired
    void setCoursePublishMapper(CoursePublishMapper coursePublishMapper) {
        this.coursePublishMapper = coursePublishMapper;
    }

    @Autowired
    void setCoursePublishService(CoursePublishService coursePublishService) {
        this.coursePublishService = coursePublishService;
    }

    @Autowired
    void setMqMessageService(MqMessageService mqMessageService) {
        this.mqMessageService = mqMessageService;
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
    @Transactional(rollbackFor = Throwable.class)
    public ResponseMessage<Boolean> commitAudit(Long companyId, Long courseId) {
        // 获取课程基本信息, 营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        CourseAssert.notNull(courseBaseInfo);
        // 校验1: 课程必须属于当前机构
        CourseAssert.companyIdValid(companyId, courseBaseInfo);
        // 校验2: 当审核状态为已提交, 则不能再次提交
        CourseAssert.auditStatusInvalid(CourseAuditStatus.SUBMITTED, courseBaseInfo, "课程正在审核");
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
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        coursePublishPre.setId(courseId);
        coursePublishPre.setCompanyId(companyId);
        coursePublishPre.setCreateDate(LocalDateTime.now());
        // 封装课程基本信息
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);
        // 封装课程营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(courseBaseInfo, courseMarket);
        // 将课程营销信息转为JSON字符串存入预发布信息
        coursePublishPre.setMarket(JsonUtil.objectToJson(courseMarket));
        // 将课程教学计划信息转为JSON字符串存入预发布信息
        coursePublishPre.setTeachplan(JsonUtil.listToJson(courseTeachPlans));
        // 获取教师信息
        LambdaQueryWrapper<CourseTeacher> courseTeacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        courseTeacherLambdaQueryWrapper.eq(CourseTeacher::getCourseId, courseId);
        // 一个课程只有一个老师教, 一个老师可以教多门课程
        CourseTeacher courseTeacher = courseTeacherMapper.selectOne(courseTeacherLambdaQueryWrapper);
        // 设置审核状态
        coursePublishPre.setStatus(CourseAuditStatus.SUBMITTED);
        // 将教师信息转成JSON存入预发布信息
        if (courseTeacher != null) {
            coursePublishPre.setTeachers(JsonUtil.objectToJson(courseTeacher));
        }
        // 先查询该课程是否已经插入到预发布表中
        int response;
        if (coursePublishPreMapper.selectById(courseId) != null) {
            response = coursePublishPreMapper.updateById(coursePublishPre);
        } else {
            // 向预发布表插入课程发布信息
            response = coursePublishPreMapper.insert(coursePublishPre);
        }
        // 设置课程的审核状态为已提交
        courseBaseInfo.setAuditStatus(CourseAuditStatus.SUBMITTED);
        courseBaseInfo.setChangeDate(LocalDateTime.now());
        // 更新课程审核状态
        if (response > 0 && courseBaseMapper.updateById(courseBaseInfo) > 0) {
            return ResponseMessage.success(Boolean.TRUE);
        } else {
            return ResponseMessage.validFail(Boolean.TRUE, "提交审核失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResponseMessage<Boolean> coursePublish(Long companyId, Long courseId) {
        // 参数校验, 获取课程基本信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        CourseAssert.notNull(courseBaseInfo);
        CourseAssert.companyIdValid(companyId, courseBaseInfo);
        // 必须是通过审核的课程才能进行发布
        CourseAssert.auditStatusValid(CourseAuditStatus.PASSED, courseBaseInfo, "请等待审核通过方可发布");
        // 保存课程发布信息
        coursePublishService.saveCoursePublishInfo(courseId);
        // 记录课程发布消息
        coursePublishService.saveCoursePublishMessage(courseId);
        // 删除课程预发布信息
        coursePublishPreMapper.deleteById(courseId);
        return ResponseMessage.success(Boolean.TRUE);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveCoursePublishInfo(Long courseId) {
        // 查询课程预发布信息
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            log.error("查询课程预发布信息失败, courseId: {}", courseId);
            ILearnException.cast("查询课程预发布信息失败");
        }
        CourseBase courseBase = new CourseBase();
        courseBase.setId(courseId);
        courseBase.setStatus(CoursePublishStatus.PUBLISHED);
        // 更新课程发布状态
        courseBaseMapper.updateById(courseBase);
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        coursePublish.setOnlineDate(LocalDateTime.now());
        coursePublish.setStatus(CoursePublishStatus.PUBLISHED);
        // 查询课程是否已存在发布信息
        if (courseEverPublished(courseId)) {
            coursePublishMapper.updateById(coursePublish);
        } else {
            coursePublishMapper.insert(coursePublish);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveCoursePublishMessage(Long courseId) {
        // 使用MqMessageSDK进行消息的插入
        MqMessage mqMessage = mqMessageService.addMessage(TaskType.COURSE_PUBLISH, String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            // 抛出异常, 好让事务进行回滚
            ILearnException.cast("插入课程发布信息失败, courseId: " + courseId);
        }
    }

    @Override
    public File generateStaticCourseHtml(Long courseId) {
        File targetFile = null;
        try {
            // 创建临时静态文件存储html
            targetFile = File.createTempFile("course" + courseId, ".html");
            log.debug("创建课程发布临时文件: {}", targetFile.getAbsolutePath());
            generateStaticCourseHtml(courseId, targetFile);
        } catch (IOException ioException) {
            log.error("创建临时文件失败, 因为: {}", ioException.getMessage());
            ILearnException.cast("创建临时文件失败");
        }
        // 返回生成的静态文件
        return targetFile;
    }

    @Override
    public void generateStaticCourseHtml(Long courseId, File targetFile) {
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            // 配置freemarker
            Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
            // 得到classpath路径
            String classpath = Objects.requireNonNull(this.getClass().getResource("/")).getPath();
            // 设置模板文件的生成路径
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            // 设置字符集
            configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
            // 指定模板文件的名称
            Template courseTemplate = configuration.getTemplate(COURSE_PUBLISH_TEMPLATE_FILE);
            // 准备数据
            CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("model", coursePreviewInfo);
            // 生成静态页面
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(courseTemplate, templateData);
            // 文件输出到目标位置
            fileOutputStream = new FileOutputStream(targetFile.getAbsolutePath());
            // 将静态化数据输出到文件中
            inputStream = IOUtils.toInputStream(content);
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (Exception exception) {
            log.error("生成发布课程静态页面出错, 因为: {}, courseId: {}", exception.getMessage(), courseId);
            ILearnException.cast("生成发布课程静态页面出错, courseId: " + courseId);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    log.error("关闭静态文件流失败, cause: {}", e.getMessage());
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("关闭静态文件流失败, cause: {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public CoursePublish getCoursePublishInfo(Long courseId) {
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        if (coursePublish == null) {
            log.error("课程尚未发布, 请发布后重试");
            ILearnException.cast("课程尚未发布");
        }
        return coursePublish;
    }

    /**
     * 判断课程之前是否已经发布过
     *
     * @param courseId 课程id
     * @return 课程是否发布过
     */
    private boolean courseEverPublished(Long courseId) {
        return coursePublishMapper.selectById(courseId) != null;
    }
}
