package com.ilearn.task.service;

import com.ilearn.base.exception.ILearnException;
import com.ilearn.task.model.po.MqMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 消息处理抽象类
 * @date 2023/2/19 10:45
 */
@Slf4j
@Data
public abstract class MessageProcessAbstract {

    private MqMessageService mqMessageService;

    @Autowired
    void setMqMessageService(MqMessageService mqMessageService) {
        this.mqMessageService = mqMessageService;
    }


    /**
     * @param mqMessage 执行任务内容
     * @return boolean true:处理成功，false处理失败
     * @description 任务处理
     * @author xiaoxiaoyi
     * @date 2023/2/19 10:47
     */
    public abstract boolean execute(MqMessage mqMessage);


    /**
     * @param shardIndex  分片序号
     * @param shardTotal  分片总数
     * @param messageType 消息类型
     * @param count       一次取出任务总数
     * @param timeout     预估任务执行时间,到此时间如果任务还没有结束则强制结束 单位秒
     * @description 扫描消息表多线程执行任务
     * @author xiaoxiaoyi
     * @date 2023/2/19 10:49
     */
    public void process(int shardIndex, int shardTotal, String messageType, int count, long timeout) {
        try {
            //扫描消息表获取任务清单
            List<MqMessage> messageList = mqMessageService.getMessageList(shardIndex, shardTotal, messageType, count);
            //任务个数
            int size = messageList.size();
            log.debug("取出待处理消息" + size + "条");
            if (size == 0) {
                return;
            }
            //创建线程池
            ExecutorService threadPool = Executors.newFixedThreadPool(size);
            //计数器
            CountDownLatch countDownLatch = new CountDownLatch(size);
            messageList.forEach(message -> threadPool.execute(() -> {
                log.debug("开始任务:{}", message);
                //处理任务
                try {
                    boolean result = execute(message);
                    if (result) {
                        log.debug("任务执行成功:{})", message);
                        //更新任务状态,删除消息表记录,添加到历史表
                        int completed = mqMessageService.completed(message.getId());
                        if (completed > 0) {
                            log.debug("任务执行成功:{}", message);
                        } else {
                            log.debug("任务执行失败:{}", message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.debug("任务出现异常:{}, 任务: {}", e.getMessage(), message);
                }
                //计数
                countDownLatch.countDown();
                log.debug("结束任务: {}", message);
            }));
            //等待,给一个充裕的超时时间,防止无限等待，到达超时时间还没有处理完成则结束任务
            if (countDownLatch.await(timeout, TimeUnit.SECONDS)) {
                log.debug("任务全部处理完成");
            } else {
                log.debug("任务全部处理完成, 超时");
            }
            System.out.println("结束....");
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("中断异常: {}", e.getMessage());
            ILearnException.cast("中断异常");
        }
    }
}
