package com.ilearn.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 预览课程信息Dto
 * @date 2/15/2023 5:02 PM
 */
@Data
@ApiModel(value = "预览课程信息Dto")
public class CoursePreviewDto {

    /**
     * 课程基本信息和营销信息
     */
    @ApiModelProperty(value = "课程基本信息和营销信息")
    private CourseBaseInfoDto courseBaseInfoDto;

    /**
     * 课程教学计划列表
     */
    private List<TeachPlanDto> teachPlanDtoList;
}
