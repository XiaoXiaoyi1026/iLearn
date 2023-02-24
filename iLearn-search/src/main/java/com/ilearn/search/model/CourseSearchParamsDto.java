package com.ilearn.search.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程搜索Dto
 * @date 2/22/2023 4:39 PM
 */
@Data
@ApiModel(value = "课程搜索Dto", description = "进行课程搜索需要传递的前端参数")
@ToString
public class CourseSearchParamsDto {

    @ApiModelProperty("关键字")
    private String keywords;

    @ApiModelProperty("大分类")
    private String mt;

    @ApiModelProperty("小分类")
    private String st;

    @ApiModelProperty("难度等级")
    private String grade;


}
