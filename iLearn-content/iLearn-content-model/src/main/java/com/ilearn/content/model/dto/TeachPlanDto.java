package com.ilearn.content.model.dto;

import com.ilearn.content.model.po.Teachplan;
import com.ilearn.content.model.po.TeachplanMedia;
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
public class TeachPlanDto extends Teachplan {

    /**
     * 教学计划Dto是一个树形结构, 这是它的子节点
     */
    private List<TeachPlanDto> teachPlanTreeNodes;

    /**
     * 这是当前教学内容关联的媒资信息
     */
    private TeachplanMedia teachplanMedia;

}
