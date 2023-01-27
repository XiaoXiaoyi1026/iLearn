package com.ilearn.content.service;

import com.ilearn.content.model.dto.CourseCategoryDto;

import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程分类相关服务
 * @date 1/27/2023 1:20 PM
 */
public interface CourseCategoryService {

    /**
     * @param id 递归开始节点的分类id
     * @return 该id下所有分类信息
     */
    List<CourseCategoryDto> queryCourseCategory(String id);

}
