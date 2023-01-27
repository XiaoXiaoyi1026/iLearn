package com.ilearn.content.model.dto;

import com.ilearn.content.model.po.CourseCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程分类Dto
 * @date 1/26/2023 5:23 PM
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CourseCategoryDto extends CourseCategory {
    /**
     * 子节点信息
     */
    private List<CourseCategoryDto> childrenTreeNodes;
}
