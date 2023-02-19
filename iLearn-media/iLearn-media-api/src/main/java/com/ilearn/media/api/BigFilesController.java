package com.ilearn.media.api;

import com.ilearn.base.dictionary.ResourceType;
import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.model.ResponseMessage;
import com.ilearn.media.model.dto.UploadFileParamsDto;
import com.ilearn.media.service.MediaFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 上传大文件用到的
 * @date 2/6/2023 5:27 PM
 */
@Api("上传大文件接口")
@Slf4j
@RestController
@RequestMapping("/upload")
public class BigFilesController {

    private final MediaFilesService mediaFilesService;

    @Autowired
    BigFilesController(MediaFilesService mediaFilesService) {
        this.mediaFilesService = mediaFilesService;
    }

    /**
     * 根据文件的MD5值检查文件是否已经上传过
     *
     * @param fileMD5 文件MD5
     * @return 文件是否已上传过
     */
    @ApiOperation(value = "根据文件的MD5值检查文件是否已经上传过")
    @PostMapping("/checkfile")
    public ResponseMessage<Boolean> checkFile(@RequestParam("fileMd5") String fileMD5) {
        return mediaFilesService.checkFile(fileMD5);
    }

    /**
     * 上传分片前检查分片是否已上传
     *
     * @param fileMD5    文件MD5
     * @param chunkIndex 分片编号
     * @return 这个分片是否已经上传过
     */
    @ApiOperation(value = "上传分片前检查分片是否已上传")
    @PostMapping("/checkchunk")
    public ResponseMessage<Boolean> checkChunk(@RequestParam("fileMd5") String fileMD5, @RequestParam("chunk") int chunkIndex) {
        return mediaFilesService.checkChunk(fileMD5, chunkIndex);
    }

    /**
     * 上传分片
     *
     * @param chunkFile  前端分好片的文件
     * @param fileMD5    源文件MD5值
     * @param chunkIndex 分片编号
     * @return 上传成功与否
     */
    @ApiOperation(value = "上传分片")
    @PostMapping("/uploadchunk")
    public ResponseMessage<Boolean> uploadChunk(@RequestParam("file") MultipartFile chunkFile, @RequestParam("fileMd5") String fileMD5, @RequestParam("chunk") int chunkIndex) {
        byte[] chunkFileBytes = null;
        try {
            chunkFileBytes = chunkFile.getBytes();
        } catch (Exception e) {
            ILearnException.cast("获取文件字节码失败, 请重试.");
        }
        return mediaFilesService.uploadChunk(fileMD5, chunkIndex, chunkFileBytes);
    }

    /**
     * 合并分片
     *
     * @param fileMD5    源文件MD5值
     * @param fileName   合并后的文件明
     * @param chunkTotal 分片总数
     * @return 上传成功与否
     */
    @ApiOperation(value = "合并分片")
    @PostMapping("/mergechunks")
    public ResponseMessage<Object> mergeChunks(@RequestParam("fileMd5") String fileMD5, @RequestParam("fileName") String fileName, @RequestParam("chunkTotal") int chunkTotal) {
        Long companyId = 1026L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFilename(fileName);
        uploadFileParamsDto.setFileType(ResourceType.VIDEO);
        return mediaFilesService.mergeChunks(companyId, fileMD5, chunkTotal, uploadFileParamsDto);
    }
}
