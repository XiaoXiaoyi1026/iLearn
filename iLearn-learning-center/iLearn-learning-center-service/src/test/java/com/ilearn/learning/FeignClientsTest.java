package com.ilearn.learning;

import com.ilearn.content.model.po.CoursePublish;
import com.ilearn.learning.feign.ContentServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 远程调用客户端测试
 * @date 3/9/2023 2:56 PM
 */
@SpringBootTest
public class FeignClientsTest {

    private ContentServiceClient contentServiceClient;

    @Autowired
    void setContentServiceClient(ContentServiceClient contentServiceClient) {
        this.contentServiceClient = contentServiceClient;
    }

    @Test
    public void testGetCoursePublishInfo() {
        CoursePublish coursePublishInfo = contentServiceClient.getCoursePublishInfo(18L);
        System.out.println(coursePublishInfo);
    }

}
