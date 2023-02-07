package com.ilearn;

import com.ilearn.base.model.PageRequestParams;
import com.ilearn.base.model.PageResponse;
import com.ilearn.content.mapper.CourseBaseMapper;
import com.ilearn.content.model.dto.CourseCategoryDto;
import com.ilearn.content.model.dto.QueryCourseParamsDto;
import com.ilearn.content.model.po.CourseBase;
import com.ilearn.content.service.CourseBaseInfoService;
import com.ilearn.content.service.CourseCategoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ContentServiceApplicationTests {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Autowired
    private CourseCategoryService courseCategoryService;

    @Test
    void testCourseBaseMapper() {
        CourseBase courseBase = courseBaseMapper.selectById(22);
        Assertions.assertNotNull(courseBase);
    }

    @Test
    void testCourseBaseInfoService() {
        PageResponse<CourseBase> courseBaseList = courseBaseInfoService.queryPageList(
                new PageRequestParams(),
                new QueryCourseParamsDto()
        );
        System.out.println(courseBaseList);
    }

    @Test
    void testCourseCategoryService() {
        List<CourseCategoryDto> treeNodes = courseCategoryService.queryCourseCategory("1");
        System.out.println(treeNodes);
    }

}
