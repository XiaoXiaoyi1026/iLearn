package com.ilearn.media.api;

import com.ilearn.base.model.ResponseMessage;
import com.ilearn.media.service.MediaFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 媒体相关的开放接口
 * @date 2/17/2023 1:09 PM
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
@RequestMapping("/open")
public class MediaOpenController {

    private MediaFilesService mediaFileService;

    @Autowired
    void setMediaFileService(MediaFilesService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    /**
     * 预览文件
     *
     * @param mediaId 媒体MD5
     * @return 文件地址
     */
    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public ResponseMessage<String> getPlayUrlByMediaId(@PathVariable String mediaId) {
        return mediaFileService.getUrl(mediaId);
    }


}
