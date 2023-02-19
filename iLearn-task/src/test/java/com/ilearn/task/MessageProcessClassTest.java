package com.ilearn.task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 消息处理类测试
 * @date 2023/2/19 10:53
 */
@SpringBootTest
public class MessageProcessClassTest {

    @Autowired
    MessageProcessClass messageProcessClass;

    @Test
    public void test() {
        System.out.println("开始执行-----》" + LocalDateTime.now());
        messageProcessClass.process(0, 1, "test", 2, 10);
        System.out.println("结束执行-----》" + LocalDateTime.now());
        try {
            Thread.sleep(90000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
