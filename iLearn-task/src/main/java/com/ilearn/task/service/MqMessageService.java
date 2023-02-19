package com.ilearn.task.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ilearn.task.model.po.MqMessage;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author xiaoxiaoyi
 * @since 2023-02-19
 */
public interface MqMessageService extends IService<MqMessage> {

    /**
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count      扫描记录数
     * @return java.util.List 消息记录
     * @description 扫描消息表记录，采用与扫描视频处理表相同的思路
     * @author xiaoxiaoyi
     * @date 2023/2/19 10:50
     */
    List<MqMessage> getMessageList(int shardIndex, int shardTotal, String messageType, int count);

    /**
     * @param businessKey1 业务id
     * @param businessKey2 业务id
     * @param businessKey3 业务id
     * @return com.ilearn.task.model.po.MqMessage 消息内容
     * @description 添加消息
     * @author xiaoxiaoyi
     * @date 2023/2/19 10:55
     */
    MqMessage addMessage(String messageType, String businessKey1, String businessKey2, String businessKey3);

    /**
     * @param id 消息id
     * @return int 更新成功：1
     * @description 完成任务
     * @author xiaoxiaoyi
     * @date 2023/2/19 11:00
     */
    int completed(long id);

    /**
     * @param id 消息id
     * @return int 更新成功：1
     * @description 完成阶段任务
     * @author xiaoxiaoyi
     * @date 2023/2/19 11:05
     */
    int completedStageOne(long id);

    int completedStageTwo(long id);

    int completedStageThree(long id);

    int completedStageFour(long id);

    /**
     * @param id 任务id
     * @return int
     * @description 查询阶段状态
     * @author xiaoxaioyi
     * @date 2023/2/19 11:03
     */
    int getStageOne(long id);

    int getStageTwo(long id);

    int getStageThree(long id);

    int getStageFour(long id);

}
