package com.ilearn.content.api;

import com.ilearn.base.model.ResponseMessage;
import com.ilearn.content.model.dto.SaveTeachPlanDto;
import com.ilearn.content.model.dto.TeachPlanBindMediaDto;
import com.ilearn.content.model.dto.TeachPlanDto;
import com.ilearn.content.model.po.TeachPlanMedia;
import com.ilearn.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    private final TeachPlanService teachPlanService;

    @Autowired
    private TeachPlanController(TeachPlanService teachPlanService) {
        this.teachPlanService = teachPlanService;
    }

    /**
     * 根据课程的id获取其对应的计划子节点
     *
     * @param courseId 教学计划id
     * @return 子节点
     */
    @ApiOperation("根据课程id查询对应的课程计划")
    @GetMapping("/{courseId}/tree-nodes")
    public List<TeachPlanDto> getTreeNodes(@PathVariable(name = "courseId") Long courseId) {
        return teachPlanService.getCourseTeachPlans(courseId);
    }

    /**
     * 添加/更新教学计划
     *
     * @param saveTeachPlanDto 要添加/更新的信息
     * @return 添加/更新结果
     */
    @ApiOperation("保存课程计划, 可以是添加或更新")
    @PostMapping
    public ResponseMessage<Boolean> saveTeachPlan(@RequestBody @Validated SaveTeachPlanDto saveTeachPlanDto) {
        return teachPlanService.saveTeachPlan(saveTeachPlanDto);
    }

    /**
     * 教学计划绑定媒体文件
     *
     * @param teachPlanBindMediaDto 教学计划和媒体文件信息
     * @return 绑定结果
     */
    @ApiOperation(value = "教学计划绑定媒体文件")
    @PostMapping("/association/media")
    public ResponseMessage<TeachPlanMedia> teachPlanBindMedia(@RequestBody TeachPlanBindMediaDto teachPlanBindMediaDto) {
        return ResponseMessage.success(teachPlanService.teachPlanBindMedia(teachPlanBindMediaDto));
    }

}
