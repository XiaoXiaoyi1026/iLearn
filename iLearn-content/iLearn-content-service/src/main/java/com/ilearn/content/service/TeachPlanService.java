package com.ilearn.content.service;

import com.ilearn.content.model.dto.SaveTeachPlanDto;
import com.ilearn.content.model.dto.TeachPlanDto;

import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 教学计划相关服务
 * @date 1/31/2023 2:10 PM
 */
public interface TeachPlanService {

    /**
     * 根据课程id查询其相关的教学计划
     *
     * @param courseId 课程id
     * @return 教学计划
     */
    List<TeachPlanDto> getTreeNodes(Long courseId);

    /**
     * 添加/修改课程教学计划
     *
     * @param saveTeachPlanDto 添加/修改的课程教学计划信息
     */
    void saveTeachPlan(SaveTeachPlanDto saveTeachPlanDto);

}
