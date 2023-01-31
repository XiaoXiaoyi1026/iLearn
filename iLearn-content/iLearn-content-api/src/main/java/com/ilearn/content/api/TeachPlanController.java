package com.ilearn.content.api;

import com.ilearn.content.model.dto.TeachPlanDto;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 根据课程计划的id获取其对应的子节点
     *
     * @param teachPlanId 教学计划id
     * @return 子节点
     */
    @GetMapping("/{teachPlanId}/tree-nodes")
    public TeachPlanDto getTreeNodes(@PathVariable(name = "teachPlanId") String teachPlanId) {
        return null;
    }

}
