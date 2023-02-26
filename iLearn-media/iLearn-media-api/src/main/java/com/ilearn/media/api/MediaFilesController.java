package com.ilearn.media.api;

import com.ilearn.base.mapper.ResourceType;
import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.model.PageRequestParams;
import com.ilearn.base.model.PageResponse;
import com.ilearn.base.model.ResponseMessage;
import com.ilearn.media.model.dto.QueryMediaParamsDto;
import com.ilearn.media.model.dto.UploadFileParamsDto;
import com.ilearn.media.model.dto.UploadFileResponseDto;
import com.ilearn.media.model.po.MediaFiles;
import com.ilearn.media.service.MediaFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

    private final MediaFilesService mediaFilesService;

    @Autowired
    private MediaFilesController(MediaFilesService mediaFilesService) {
        this.mediaFilesService = mediaFilesService;
    }

    /**
     * 媒资列表查询接口
     *
     * @param pageRequestParams   页面参数
     * @param queryMediaParamsDto 查询条件
     * @return 页面数据
     */
    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResponse<MediaFiles> list(PageRequestParams pageRequestParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFilesService.queryMediaFiles(companyId, pageRequestParams, queryMediaParamsDto);
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
    @ApiOperation(value = "上传媒资文件(图片/文档/视频)")
    @RequestMapping(value = "/upload/coursefile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public UploadFileResponseDto upload(@NotNull @RequestPart("filedata") MultipartFile fileData, @RequestParam(value = "folder", required = false) String folder, @RequestParam(value = "objectName", required = false) String objectName) {
        Long companyId = 1026L;
        UploadFileResponseDto uploadFileResponseDto = null;
        try {
            UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
            String contentType = fileData.getContentType();
            if (contentType == null) {
                ILearnException.cast("文件的contentType不能为空!");
            }
            uploadFileParamsDto.setContentType(contentType);
            uploadFileParamsDto.setFileSize(fileData.getSize());
            String fileName = fileData.getOriginalFilename();
            uploadFileParamsDto.setFilename(fileName);
            if (contentType.contains("image")) {
                uploadFileParamsDto.setFileType(ResourceType.IMAGE);
            } else {
                uploadFileParamsDto.setFileType(ResourceType.OTHER);
            }
            /* 因为这里的mediaFileService是被Spring代理的, 所以异常会被捕捉 */
            uploadFileResponseDto = mediaFilesService.uploadFile(companyId, uploadFileParamsDto, fileData.getBytes(), folder, objectName);
        } catch (IOException e) {
            log.error("获取文件字节码失败, 因为: {}", e.getMessage());
            e.printStackTrace();
            ILearnException.cast("获取文件字节码失败, 请重试.");
        }
        return uploadFileResponseDto;
    }

    /**
     * 预览文件
     *
     * @param mediaId 媒体MD5
     * @return 媒体播放地址
     */
    @ApiOperation(value = "预览文件")
    @GetMapping(value = "/preview/{mediaId}")
    public ResponseMessage<String> preview(@PathVariable("mediaId") String mediaId) {
        // 根据mediaId查询到文件的URL返回给前端
        return mediaFilesService.getUrl(mediaId);
    }
}
