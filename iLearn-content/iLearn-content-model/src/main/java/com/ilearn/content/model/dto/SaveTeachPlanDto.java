package com.ilearn.content.model.dto;

import com.ilearn.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 保存课程计划Dto
 * @date 1/31/2023 2:56 PM
 */
@Data
@ApiModel(value = "保存课程计划Dto", description = "在更新或者添加课程计划时用到")
public class SaveTeachPlanDto {

    /**
     * 课程计划id
     */
    @ApiModelProperty("课程计划id")
    @NotNull(message = "更新课程计划id不能为空!", groups = {ValidationGroups.Update.class})
    private Long id;

    /**
     * 课程计划名称
     */
    @ApiModelProperty(value = "课程计划名称", required = true)
    @NotNull
    @NotEmpty
    private String pname;

    /**
     * 课程计划父级Id
     */
    @ApiModelProperty(value = "课程计划父级Id", required = true)
    @NotNull
    private Long parentid;

    /**
     * 层级，分为1、2、3级
     */
    @ApiModelProperty(value = "层级，分为1、2、3级", required = true)
    @NotNull
    private Integer grade;

    /**
     * 课程类型:1视频、2文档
     */
    @ApiModelProperty(value = "课程类型:1视频、2文档")
    private String mediaType;

    /**
     * 开始直播时间
     */
    @ApiModelProperty(value = "开始直播时间")
    private LocalDateTime startTime;

    /**
     * 直播结束时间
     */
    @ApiModelProperty(value = "直播结束时间")
    private LocalDateTime endTime;

    /**
     * 课程标识
     */
    @ApiModelProperty(value = "课程标识", required = true)
    @NotNull
    private Long courseId;

    /**
     * 课程发布标识
     */
    @ApiModelProperty(value = "课程发布标识")
    private Long coursePubId;

    /**
     * 是否支持试看
     */
    @ApiModelProperty(value = "是否支持试看")
    private String isPreview;

}
