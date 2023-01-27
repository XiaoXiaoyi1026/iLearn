package com.ilearn.content.api;

import com.ilearn.content.model.dto.CourseCategoryDto;
import com.ilearn.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程分类
 * @date 1/26/2023 5:11 PM
 */

@RestController
@Api(value = "课程分类", tags = "课程分类相关接口")
@RequestMapping("/course-category")
@Slf4j
public class CourseCategoryController {

    @Autowired
    private CourseCategoryService courseCategoryService;

    /**
     * 课程分类查询
     *
     * @return 课程分类的树形节点信息
     */
    @GetMapping("/tree-nodes")
    @ApiOperation("课程分类查询接口")
    public List<CourseCategoryDto> treeNodes() {
        return courseCategoryService.queryCourseCategory("1");
    }

}
