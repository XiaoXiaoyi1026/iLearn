package com.ilearn.media.service;

import com.ilearn.base.model.PageParams;
import com.ilearn.base.model.PageResult;
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
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.ilearn.base.model.PageResult<com.ilearn.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @author xiaoxiaoyi
     * @date 2022/9/10 8:57
     */
    PageResult<MediaFiles> queryMediaFiles(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传媒资文件(图片/文档/视频), 这是所有模块通用的上传文件服务
     *
     * @param companyId           公司名称
     * @param uploadFileParamsDto 上传的文件信息
     * @param fileDataBytes       文件的字节数据
     * @param folder              文件在MinIO上的存储目录
     * @param objectName          文件在MinIO上的存储名称
     * @return 上传文件响应Dto
     */
    UploadFileResponseDto uploadFile(@NotNull(message = "公司id不能为空!") Long companyId,
                                     @Valid UploadFileParamsDto uploadFileParamsDto,
                                     byte[] fileDataBytes, String folder, String objectName);
}
