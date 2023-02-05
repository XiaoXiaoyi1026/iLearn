package com.ilearn.media.model.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 上传普通文件请求参数
 * @date 2023/2/4 11:50
 */
@Data
@ToString
public class UploadFileParamsDto {

    /**
     * 文件名称
     */
    @NotNull(message = "Filename must not be null!")
    private String filename;

    /**
     * 文件content-type
     */
    @NotNull(message = "File content type must not be null!")
    private String contentType;

    /**
     * 文件类型（文档，音频，视频）
     */
    @NotNull(message = "File type must not be null!")
    private String fileType;
    /**
     * 文件大小
     */
    @NotNull(message = "File size must not be null!")
    private Long fileSize;

    /**
     * 标签
     */
    private String tags;

    /**
     * 上传人
     */
    private String username;

    /**
     * 备注
     */
    private String remark;


}
