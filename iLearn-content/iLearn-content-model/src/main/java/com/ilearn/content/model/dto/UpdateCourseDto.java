package com.ilearn.content.model.dto;

import com.ilearn.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 更新课程Dto
 * @date 1/29/2023 11:20 AM
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "更新课程Dto", description = "更新课程基本信息")
public class UpdateCourseDto extends AddCourseDto {

    @NotNull(message = "更新课程ID不能为空", groups = {ValidationGroups.Update.class})
    @ApiModelProperty(value = "课程ID", required = true)
    private Long id;

}
