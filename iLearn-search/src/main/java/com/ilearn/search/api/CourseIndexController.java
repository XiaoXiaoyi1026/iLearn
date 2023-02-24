package com.ilearn.search.api;

import com.ilearn.base.exception.ILearnException;
import com.ilearn.search.model.CourseIndex;
import com.ilearn.search.service.CourseIndexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程索引控制器
 * @date 2/22/2023 4:02 PM
 */
@Slf4j
@Api(value = "课程信息索引接口", tags = "课程信息索引接口, 负责跟elasticsearch进行通信")
@RestController
@RequestMapping("/index")
public class CourseIndexController {

    private CourseIndexService courseIndexService;

    /**
     * 课程索引的索引库名称
     */
    @Value("${elasticsearch.course.index}")
    private String courseIndexName;

    @Autowired
    void setCourseIndexService(CourseIndexService courseIndexService) {
        this.courseIndexService = courseIndexService;
    }

    @ApiOperation("添加课程索引")
    @PostMapping("/course")
    public Boolean add(@RequestBody @NotNull CourseIndex courseIndex) {
        String id = String.valueOf(courseIndex.getId());
        if (id.equals("null")) {
            ILearnException.cast("课程id为空");
        }
        Boolean addCourseIndexResult = courseIndexService.addCourseIndex(courseIndexName, String.valueOf(courseIndex.getId()), courseIndex);
        if (!addCourseIndexResult) {
            log.error("添加课程索引失败, courseIndex: {}", courseIndex);
            ILearnException.cast("添加课程索引失败");
        }
        return Boolean.TRUE;
    }
}