package com.ilearn.media.service;

import com.ilearn.base.model.PageParams;
import com.ilearn.base.model.PageResult;
import com.ilearn.media.model.dto.QueryMediaParamsDto;
import com.ilearn.media.model.po.MediaFiles;

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


}
