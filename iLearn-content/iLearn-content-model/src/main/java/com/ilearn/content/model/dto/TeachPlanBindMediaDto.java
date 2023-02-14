package com.ilearn.content.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 教学计划绑定媒体文件Dto, 用于接收前端传过来的参数
 * @date 2/14/2023 10:42 AM
 */
@Data
@ApiModel(value = "教学计划绑定媒体用的Dto", description = "接收前端教学计划绑定媒体的相关参数")
public class TeachPlanBindMediaDto {

    @ApiModelProperty(value = "媒体文件id", required = true)
    @JsonProperty(value = "mediaId")
    private String mediaId;

    @ApiModelProperty(value = "媒体文件名称", required = true)
    @JsonProperty(value = "fileName")
    private String mediaFileName;

    @ApiModelProperty(value = "教学计划id", required = true)
    @JsonProperty(value = "teachplanId")
    private Long teachPlanId;

}
