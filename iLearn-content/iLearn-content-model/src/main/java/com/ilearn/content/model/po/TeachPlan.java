package com.ilearn.content.model.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程计划
 * </p>
 *
 * @author xiaoxiaoyi
 */
@Data
@TableName("teachplan")
@ApiModel(value = "课程教学计划 PO", description = "对应数据表teachplan")
public class TeachPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("教学计划id, 主键")
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
     * 章节及课程时介绍
     */
    @ApiModelProperty("章节及课程时介绍")
    private String description;

    /**
     * 时长，单位时:分:秒
     */
    @ApiModelProperty("时长，单位时:分:秒")
    private String timelength;

    /**
     * 排序字段
     */
    @ApiModelProperty("排序字段")
    private Integer orderby;

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
     * 状态（1正常  0删除）
     */
    @ApiModelProperty("状态（1正常  0删除）")
    private Integer status;

    /**
     * 是否支持试学或预览（试看）
     */
    @ApiModelProperty("是否支持试学或预览（试看）")
    private String isPreview;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("创建时间")
    private LocalDateTime createDate;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime changeDate;


}
