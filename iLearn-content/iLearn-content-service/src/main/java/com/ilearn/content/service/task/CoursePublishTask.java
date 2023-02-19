package com.ilearn.content.service.task;

import com.ilearn.base.dictionary.TaskType;
import com.ilearn.task.model.po.MqMessage;
import com.ilearn.task.service.MessageProcessAbstract;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程发布任务
 * @date 2/19/2023 11:54 AM
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    /**
     * 分片任务调度入口
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
    public boolean execute(@NotNull MqMessage mqMessage) {
        // 课程发布执行逻辑
        log.debug("开始执行发布课程任务, taskId: {}, 课程id: {}", mqMessage.getId(), mqMessage.getBusinessKey1());
        // 将课程页面信息进行静态化
        // 上传到MinIO
        return Boolean.TRUE;
    }
}
