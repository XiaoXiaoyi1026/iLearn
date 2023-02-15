package com.ilearn.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 查询课程参数
 * @date 1/24/2023 4:48 PM
 */
@Data
@ToString
public class QueryCourseParamsDto {

    // 审核状态
    @ApiModelProperty("课程当前审核状态")
    private String auditStatus;
    // 课程名称
    @ApiModelProperty("课程名称")
    private String courseName;
    // 发布状态
    @ApiModelProperty("课程发布状态")
    private String publishStatus;

}
