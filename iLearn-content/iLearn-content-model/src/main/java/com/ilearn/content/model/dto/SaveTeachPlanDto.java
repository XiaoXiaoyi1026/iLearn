package com.ilearn.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
    private Long id;

    /**
     * 课程计划名称
     */
    @ApiModelProperty("课程计划名称")
    private String pname;

    /**
     * 课程计划父级Id
     */
    @ApiModelProperty("课程计划父级Id")
    private Long parentid;

    /**
     * 层级，分为1、2、3级
     */
    @ApiModelProperty("层级，分为1、2、3级")
    private Integer grade;

    /**
     * 课程类型:1视频、2文档
     */
    @ApiModelProperty("课程类型:1视频、2文档")
    private String mediaType;

    /**
     * 开始直播时间
     */
    @ApiModelProperty("开始直播时间")
    private LocalDateTime startTime;

    /**
     * 直播结束时间
     */
    @ApiModelProperty("直播结束时间")
    private LocalDateTime endTime;

    /**
     * 课程标识
     */
    @ApiModelProperty("课程标识")
    private Long courseId;

    /**
     * 课程发布标识
     */
    @ApiModelProperty("课程发布标识")
    private Long coursePubId;

    /**
     * 是否支持试看
     */
    @ApiModelProperty("是否支持试看")
    private String isPreview;

}
