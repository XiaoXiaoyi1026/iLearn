package com.ilearn.content;

import com.ilearn.content.feign.MediaServiceClient;
import com.ilearn.content.feign.MultipartSupportConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 测试Feign远程调用
 * @date 2/21/2023 2:04 PM
 */
@SpringBootTest
public class FeignTest {

    @Autowired
    private MediaServiceClient mediaServiceClient;

    @Test
    public void testUploadMedia() {
        // 源文件
        File file = new File("D:\\Download\\Java\\SpringCloud\\iLearn\\iLearn\\iLearn-content\\iLearn-content-service\\src\\test\\resources\\templates\\test.html");
        // 分片文件
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        // 执行上传
        String test = mediaServiceClient.upload(multipartFile, "test", "test.html");
        System.out.println(test);
    }

}
