package com.ilearn.search.api;

import com.ilearn.base.model.PageRequestParams;
import com.ilearn.search.model.dto.CourseSearchParamsDto;
import com.ilearn.search.model.dto.CourseSearchResultDto;
import com.ilearn.search.model.po.CourseIndex;
import com.ilearn.search.service.CourseSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程搜索控制器
 * @date 2/22/2023 4:42 PM
 */
@Slf4j
@RestController
@RequestMapping("/course")
@Api(value = "课程搜索接口", tags = "课程搜索接口")
public class CourseSearchController {

    private CourseSearchService courseSearchService;


    @Autowired
    void setCourseSearchService(CourseSearchService courseSearchService) {
        this.courseSearchService = courseSearchService;
    }

    @ApiOperation("课程搜索列表")
    @GetMapping("/list")
    public CourseSearchResultDto<CourseIndex> list(PageRequestParams pageRequestParams, CourseSearchParamsDto courseSearchParamsDto) {
        return courseSearchService.queryCoursePubIndex(pageRequestParams, courseSearchParamsDto);
    }
}

