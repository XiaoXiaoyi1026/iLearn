package com.ilearn.media.api;

import com.ilearn.base.dictionary.ResourceType;
import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.model.PageParams;
import com.ilearn.base.model.PageResult;
import com.ilearn.media.model.dto.QueryMediaParamsDto;
import com.ilearn.media.model.dto.UploadFileParamsDto;
import com.ilearn.media.model.dto.UploadFileResponseDto;
import com.ilearn.media.model.po.MediaFiles;
import com.ilearn.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2023/3/2 15:25
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@Slf4j
@RestController
public class MediaFilesController {

    private final MediaFileService mediaFileService;

    @Autowired
    private MediaFilesController(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiles(companyId, pageParams, queryMediaParamsDto);
    }

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
    @ApiOperation("上传媒资文件(图片/文档/视频)")
    @RequestMapping(value = "/upload/coursefile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public UploadFileResponseDto upload(@RequestPart("filedata") MultipartFile fileData,
                                        @RequestParam(value = "folder", required = false) String folder,
                                        @RequestParam(value = "objectName", required = false) String objectName) {
        Long companyId = 1232141425L;
        UploadFileResponseDto uploadFileResponseDto = null;
        try {
            UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
            String contentType = fileData.getContentType();
            if (contentType == null) {
                ILearnException.cast("文件的contentType不能为空!");
            }
            uploadFileParamsDto.setContentType(contentType);
            uploadFileParamsDto.setFileSize(fileData.getSize());
            uploadFileParamsDto.setFilename(fileData.getOriginalFilename());
            if (contentType.contains("image")) {
                uploadFileParamsDto.setFileType(ResourceType.IMAGE);
            } else {
                uploadFileParamsDto.setFileType(ResourceType.OTHER);
            }
            /* 因为这里的mediaFileService是被Spring代理的, 所以异常会被捕捉 */
            uploadFileResponseDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, fileData.getBytes(), folder, objectName);
        } catch (IOException e) {
            log.error("获取文件字节码失败, 因为: {}", e.getMessage());
            e.printStackTrace();
            ILearnException.cast("获取文件字节码失败, 请重试.");
        }
        return uploadFileResponseDto;
    }
}
