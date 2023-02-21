package com.ilearn.content.feign;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description Feign访问媒体服务的降级处理
 * @date 2/21/2023 3:22 PM
 */
public class MediaServiceClientFallback implements MediaServiceClient {

    /**
     * 降级方法(下游(iLearn-media-api)发生熔断后执行)
     *
     * @param fileData   媒体文件(分成多部分存储)
     *                   不是简单的key-value结构, 因而需要使用注解@RequestPart进行标记
     * @param folder     文件在MinIO上的存储目录
     * @param objectName 文件在MinIO上的存储名称
     * @return json字符串
     */
    @Override
    public String upload(@NotNull MultipartFile fileData, String folder, String objectName) {
        // 因为拿不到异常信息所以不用该方式
        return null;
    }
}
