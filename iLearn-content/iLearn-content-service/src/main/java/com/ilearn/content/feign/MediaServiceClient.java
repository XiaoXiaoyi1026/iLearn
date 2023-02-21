package com.ilearn.content.feign;

import com.ilearn.content.config.MultipartSupportConfig;
import io.swagger.annotations.ApiOperation;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 通过Feign远程调用media相关的服务
 * @date 2/21/2023 10:58 AM
 */
@FeignClient(name = "iLearn-media-api", configuration = {MultipartSupportConfig.class}, url = "localhost:63050", path = "/media", fallback = MediaServiceClientFallback.class)
public interface MediaServiceClient {

    /**
     * 上传媒资文件(图片/文档/视频)
     * consume指定参数类型
     *
     * @param fileData   媒体文件(分成多部分存储)
     *                   不是简单的key-value结构, 因而需要使用注解@RequestPart进行标记
     * @param folder     文件在MinIO上的存储目录
     * @param objectName 文件在MinIO上的存储名称
     * @return 上传文件响应
     */
    @ApiOperation(value = "上传媒资文件(图片/文档/视频)")
    @RequestMapping(value = "/upload/coursefile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    String upload(@NotNull @RequestPart("filedata") MultipartFile fileData, @RequestParam(value = "folder", required = false) String folder, @RequestParam(value = "objectName", required = false) String objectName);

}
