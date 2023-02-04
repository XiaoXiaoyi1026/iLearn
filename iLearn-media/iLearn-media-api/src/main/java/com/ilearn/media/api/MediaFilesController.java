package com.ilearn.media.api;

import com.ilearn.base.model.PageParams;
import com.ilearn.base.model.PageResult;
import com.ilearn.media.model.dto.QueryMediaParamsDto;
import com.ilearn.media.model.dto.UploadFileResponseDto;
import com.ilearn.media.model.po.MediaFiles;
import com.ilearn.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2023/3/2 15:25
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {

    @Autowired
    MediaFileService mediaFileService;

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
     * @param folder     文件存储路径, 对应MinIO上的
     * @param objectName 文件名称, 对应本地的绝对路径
     * @return 上传文件响应
     */
    @ApiOperation("上传媒资文件(图片/文档/视频)")
    @RequestMapping(value = "/upload/coursefile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public UploadFileResponseDto upload(@RequestPart("filedata") MultipartFile fileData,
                                        @RequestParam("folder") String folder,
                                        @RequestParam("objectName") String objectName) {
        return null;
    }

}
