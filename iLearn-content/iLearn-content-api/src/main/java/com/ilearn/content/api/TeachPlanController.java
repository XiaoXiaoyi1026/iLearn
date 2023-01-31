package com.ilearn.content.api;

import com.ilearn.content.model.dto.TeachPlanDto;
import com.ilearn.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程教学计划相关接口
 * @date 1/31/2023 10:01 AM
 */
@RequestMapping("/teachplan")
@Api(value = "教学计划相关接口", tags = "课程教学计划相关接口")
@RestController
@Slf4j
public class TeachPlanController {

    @Autowired
    private TeachPlanService teachPlanService;

    /**
     * 根据课程的id获取其对应的计划子节点
     *
     * @param courseId 教学计划id
     * @return 子节点
     */
    @GetMapping("/{courseId}/tree-nodes")
    public List<TeachPlanDto> getTreeNodes(@PathVariable(name = "courseId") Long courseId) {
        return teachPlanService.getTreeNodes(courseId);
    }

}
