package com.ilearn.content.model.dto;

import com.ilearn.content.model.po.TeachPlan;
import com.ilearn.content.model.po.TeachPlanMedia;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 每一门课程的教学计划管理Dto
 * @date 1/31/2023 9:57 AM
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "课程教学计划 Dto", description = "查询课程教学计划")
public class TeachPlanDto extends TeachPlan {

    /**
     * 教学计划Dto是一个树形结构, 这是它的子节点
     */
    @ApiModelProperty("课程教学计划的子节点")
    private List<TeachPlanDto> teachPlanTreeNodes;

    /**
     * 这是当前教学内容关联的媒资信息
     */
    @ApiModelProperty("课程教学计划媒资信息")
    private TeachPlanMedia teachplanMedia;

}
