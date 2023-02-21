package com.ilearn.content.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 使Feign支持Multipart数据格式
 * @date 2023/2/21 11:01
 */
@Configuration
public class MultipartSupportConfig {

    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Autowired
    void setMessageConverters(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    /**
     * Primary注解: 注入相同类型的Bean时优先使用
     *
     * @return feign编码
     */
    @Bean
    @Primary
    @Scope("prototype")
    public Encoder feignEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    /**
     * 将file转为Multipart
     *
     * @param file 源文件对象
     * @return 分片文件对象
     */
    @Contract("_ -> new")
    public static @NotNull MultipartFile getMultipartFile(@NotNull File file) {
        // 创建新的磁盘文件
        FileItem item = new DiskFileItemFactory().createItem("file", MediaType.MULTIPART_FORM_DATA_VALUE, true, file.getName());
        try (
                // 创建文件的输入流
                FileInputStream inputStream = new FileInputStream(file);
                // 创建磁盘文件的输出流
                OutputStream outputStream = item.getOutputStream()
        ) {
            // 通过流拷贝进行文件传输
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 将磁盘上的文件转换成MultipartFile返回
        return new CommonsMultipartFile(item);
    }
}
