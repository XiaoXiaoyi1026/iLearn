package com.ilearn.base.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 日期和时间格式配置
 * @date 1/24/2023 6:12 PM
 */
@Configuration
public class LocalDateTimeConfig {

    /*
     * 序列化内容
     *  LocalDateTime -> String
     * 服务端返回给客户端的内容
     * */
    @Bean
    public LocalDateTimeSerializer localDateTimeSerializer() {
        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /*
     * 反序列化内容
     *  String -> LocalDateTime
     * 客户端传给服务端的数据
     * */
    @Bean
    public LocalDateTimeDeserializer localDateTimeDeserializer() {
        return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /*
     * 配置
     * */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
                return builder -> {
                    builder.serializerByType(LocalDateTime.class, localDateTimeSerializer());
                    builder.deserializerByType(LocalDateTime.class, localDateTimeDeserializer());
        };
    }

}
