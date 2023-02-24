package com.ilearn.content.service.task;

import com.ilearn.base.dictionary.TaskType;
import com.ilearn.base.exception.ILearnException;
import com.ilearn.content.config.MultipartSupportConfig;
import com.ilearn.content.feign.MediaServiceClient;
import com.ilearn.content.feign.SearchServiceClient;
import com.ilearn.content.feign.model.CourseIndex;
import com.ilearn.content.model.po.CoursePublish;
import com.ilearn.content.service.CoursePublishService;
import com.ilearn.task.model.po.MqMessage;
import com.ilearn.task.service.MessageProcessAbstract;
import com.ilearn.task.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程发布任务
 * @date 2/19/2023 11:54 AM
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    private CoursePublishService coursePublishService;

    private MediaServiceClient mediaServiceClient;

    private MqMessageService mqMessageService;

    private SearchServiceClient searchServiceClient;

    @Autowired
    void setCoursePublishService(CoursePublishService coursePublishService) {
        this.coursePublishService = coursePublishService;
    }

    @Autowired
    void setMediaServiceClient(MediaServiceClient mediaServiceClient) {
        this.mediaServiceClient = mediaServiceClient;
    }

    @Autowired
    void setMqMessageService(MqMessageService mqMessageService) {
        this.mqMessageService = mqMessageService;
    }

    @Autowired
    void setSearchServiceClient(SearchServiceClient searchServiceClient) {
        this.searchServiceClient = searchServiceClient;
    }

    /**
     * 分片课程发布任务调度入口
     */
    @XxlJob("coursePublishTaskHandler")
    public void coursePublishTaskHandler() {
        log.debug("开始执行发布课程任务");
        // 获取分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex: {}, shardTotal: {}", shardIndex, shardTotal);
        process(shardIndex, shardTotal, TaskType.COURSE_PUBLISH, 5, 60);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean execute(@NotNull MqMessage mqMessage) {
        // 课程发布执行逻辑
        Long courseId = Long.valueOf(mqMessage.getBusinessKey1());
        log.debug("开始执行发布课程任务, 课程id: {}", courseId);
        // 任务阶段1: 课程静态化
        courseStatic(mqMessage, courseId);
        // 任务阶段2: 将课程索引信息上传到索引库(elasticsearch)
        courseIndex(mqMessage, courseId);
        // 用Redis将课程信息进行缓存
        return Boolean.TRUE;
    }


    /**
     * 课程静态化
     *
     * @param mqMessage 任务消息
     * @param courseId  课程id
     */
    public void courseStatic(@NotNull MqMessage mqMessage, Long courseId) {
        Long taskId = mqMessage.getId();
        // 判断任务有没有完成
        if (mqMessageService.stageIsCompleted(taskId, 1)) {
            log.debug("课程静态化任务已完成, 任务信息: {}", mqMessage);
            return;
        }
        // 将课程页面信息进行静态化并上传到MinIO
        File staticCourseHtml = coursePublishService.generateStaticCourseHtml(courseId);
        if (staticCourseHtml == null) {
            log.error("生成课程发布静态html页面失败, 任务信息: {}", mqMessage);
            ILearnException.cast("生成课程发布静态html页面失败");
        }
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(staticCourseHtml);
        // 删除临时文件
        if (staticCourseHtml.exists() && !staticCourseHtml.isDirectory() && staticCourseHtml.delete()) {
            log.debug("删除静态临时文件: {}", staticCourseHtml.getAbsolutePath());
        } else {
            log.error("删除静态临时文件失败: {}", staticCourseHtml.getAbsolutePath());
        }
        String uploadResult = mediaServiceClient.upload(multipartFile, "course", courseId + ".html");
        if (uploadResult == null) {
            // 走的降级方法
            log.error("上传课程发布静态html页面失败, 任务信息: {}", mqMessage);
            ILearnException.cast("上传课程发布静态html页面失败");
        }
        // 一阶段任务完成
        if (mqMessageService.completedStageOne(taskId) > 0) {
            log.debug("课程发布一阶段, 静态化发布课程页面成功 任务信息: {}", mqMessage);
        } else {
            log.error("课程发布一阶段, 静态化发布课程页面失败 任务信息: {}", mqMessage);
            ILearnException.cast("课程发布一阶段失败");
        }
    }

    /**
     * 添加课程索引
     *
     * @param mqMessage 任务消息
     * @param courseId  课程id
     */
    public void courseIndex(@NotNull MqMessage mqMessage, Long courseId) {
        Long taskId = mqMessage.getId();
        // 判断任务有没有完成
        if (mqMessageService.stageIsCompleted(taskId, 2)) {
            log.debug("课程索引已创建完成, 任务信息: {}", mqMessage);
            return;
        }
        // 将课程信息封装为CourseIndex并远程调用搜索服务完成索引添加
        CourseIndex courseIndex = new CourseIndex();
        CoursePublish coursePublishInfo = coursePublishService.getCoursePublishInfo(courseId);
        BeanUtils.copyProperties(coursePublishInfo, courseIndex);
        if (searchServiceClient.add(courseIndex)) {
            log.debug("添加索引成功 任务信息: {}", mqMessage);
        } else {
            log.error("添加索引失败 任务信息: {}", mqMessage);
            ILearnException.cast("添加索引失败");
        }
        // 二阶段任务完成
        if (mqMessageService.completedStageTwo(taskId) > 0) {
            log.debug("课程发布二阶段成功 任务信息: {}", mqMessage);
        } else {
            log.error("课程发布二阶段失败 任务信息: {}", mqMessage);
            ILearnException.cast("课程发布二阶段, 创建课程索引失败");
        }
    }

}
