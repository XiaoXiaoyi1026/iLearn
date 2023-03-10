package com.ilearn.task;

import com.ilearn.task.model.po.MqMessage;
import com.ilearn.task.service.MessageProcessAbstract;
import com.ilearn.task.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xiaoxaioyi
 * @version 1.0
 * @description 消息处理测试类，继承MessageProcessAbstract
 * @date 2023/2/19 10:53
 */
@Slf4j
@Component
public class MessageProcessClass extends MessageProcessAbstract {

    private MqMessageService mqMessageService;

    @Autowired
    void setMqMessageService(MqMessageService mqMessageService) {
        this.mqMessageService = mqMessageService;
    }

    @Override
    public boolean execute(MqMessage mqMessage) {
        Long id = mqMessage.getId();
        log.debug("开始执行任务:{}", id);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //取出阶段状态
        int stageOne = mqMessageService.getStageOne(id);
        if (stageOne < 1) {
            log.debug("开始执行第一阶段任务");
            System.out.println();
            int i = mqMessageService.completedStageOne(id);
            if (i > 0) {
                log.debug("完成第一阶段任务");
            }

        } else {
            log.debug("无需执行第一阶段任务");
        }

        return true;
    }
}
