package com.ilearn.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ilearn.content.model.dto.TeachPlanDto;
import com.ilearn.content.model.po.TeachPlan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author xiaoxiaoyi
 */
@Mapper
public interface TeachPlanMapper extends BaseMapper<TeachPlan> {
    /**
     * 根据课程id获取其教学计划的子节点
     *
     * @param courseId 课程id
     * @return 教学计划的子节点
     */
    List<TeachPlanDto> getTreeNodes(Long courseId);
}
