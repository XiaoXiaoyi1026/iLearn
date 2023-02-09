package com.ilearn.media.service;

import com.ilearn.base.model.PageRequestParams;
import com.ilearn.base.model.PageResponse;
import com.ilearn.base.model.ResponseMessage;
import com.ilearn.media.model.dto.QueryMediaParamsDto;
import com.ilearn.media.model.dto.UploadFileParamsDto;
import com.ilearn.media.model.dto.UploadFileResponseDto;
import com.ilearn.media.model.po.MediaFiles;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2023/2/3 15:49
 */
public interface MediaFilesService {

    /**
     * @param pageRequestParams   分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.ilearn.base.model.PageResponse<com.ilearn.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @author xiaoxiaoyi
     * @date 2022/9/10 8:57
     */
    PageResponse<MediaFiles> queryMediaFiles(Long companyId, PageRequestParams pageRequestParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传媒资文件(图片/文档/视频), 这是所有模块通用的上传文件服务
     *
     * @param companyId           公司名称
     * @param uploadFileParamsDto 上传的文件信息
     * @param fileData            文件的字节数据
     * @param folder              文件在MinIO上的存储目录
     * @param objectName          文件在MinIO上的存储名称
     * @return 上传文件响应Dto
     */
    UploadFileResponseDto uploadFile(@NotNull(message = "公司id不能为空!") Long companyId, @Valid UploadFileParamsDto uploadFileParamsDto, byte[] fileData, String folder, String objectName);

    /**
     * 保存媒体信息到数据库
     *
     * @param companyId           公司id
     * @param uploadFileParamsDto 上传文件的参数
     * @param fileId              文件的id
     * @param bucketName          桶名称
     * @param objectName          全路径, 即保存在服务器上的位置
     * @return 保存的文件信息
     */
    MediaFiles saveFileInformation2DataBase(Long companyId, UploadFileParamsDto uploadFileParamsDto, String fileId, String bucketName, String objectName);

    /**
     * 根据文件的MD5值检查文件是否已经上传过
     *
     * @param sourceFileMD5 要上传文件的MD5值
     * @return 文件是否已经上传
     */
    ResponseMessage<Boolean> checkFile(String sourceFileMD5);

    /**
     * 上传分片前检查分片是否已上传
     *
     * @param sourceFileMD5 源文件MD5值
     * @param chunkIndex    分片编号
     * @return 分片是否已上传
     */
    ResponseMessage<Boolean> checkChunk(String sourceFileMD5, int chunkIndex);

    /**
     * 上传分片文件
     *
     * @param sourceFileMD5 源文件MD5
     * @param chunkIndex    分片序号
     * @param fileData      源文件字节码
     * @return 响应消息
     */
    ResponseMessage<Boolean> uploadChunk(String sourceFileMD5, int chunkIndex, byte[] fileData);

    /**
     * 合并分片文件
     *
     * @param companyId           机构id
     * @param sourceFileMD5       源文件MD5
     * @param chunkTotal          分片的数量
     * @param uploadFileParamsDto 上传文件的参数Dto
     * @return 合并结果
     */
    ResponseMessage<Object> mergeChunks(Long companyId, String sourceFileMD5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);

    /**
     * 根据文件id拿到文件的url, 即MinIO访问路径
     *
     * @param fileId 文件id
     * @return 文件MinIO的Url
     */
    ResponseMessage<String> getUrl(String fileId);
}
