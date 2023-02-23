package com.ilearn.search.service;

import com.ilearn.base.model.PageRequestParams;
import com.ilearn.search.model.dto.CourseSearchParamsDto;
import com.ilearn.search.model.dto.CourseSearchResultDto;
import com.ilearn.search.model.po.CourseIndex;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程搜索服务
 * @date 2/22/2023 4:50 PM
 */
public interface CourseSearchService {
    /**
     * 查询发布课程
     *
     * @param pageRequestParams     页面参数
     * @param courseSearchParamsDto 课程搜索参数
     * @return 课程集合
     */
    CourseSearchResultDto<CourseIndex> queryCoursePubIndex(PageRequestParams pageRequestParams, CourseSearchParamsDto courseSearchParamsDto);
}
