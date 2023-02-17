package com.ilearn.content.api;

import com.ilearn.content.model.dto.CoursePreviewDto;
import com.ilearn.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程发布控制器
 * @date 2/15/2023 3:43 PM
 */
@Controller
@Api(value = "课程发布相关接口")
public class CoursePublishController {

    private CoursePublishService coursePublishService;

    @Autowired
    void setCoursePublishService(CoursePublishService coursePublishService) {
        this.coursePublishService = coursePublishService;
    }

    /**
     * 课程预览
     *
     * @param courseId 要预览的课程id
     * @return 预览的静态页面
     */
    @ApiOperation(value = "课程预览")
    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView coursePreview(@PathVariable(name = "courseId") Long courseId) {
        // 绑定模板文件
        ModelAndView mav = new ModelAndView("course_template");
        // 查询模板数据
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
        // 模板绑定数据
        mav.addObject("model", coursePreviewInfo);
        return mav;
    }

    /**
     * 课程提交审核
     *
     * @param courseId 提交的课程id
     */
    @ApiOperation(value = "课程提交审核")
    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId) {
        Long companyId = 1026L;
        coursePublishService.commitAudit(companyId, courseId);
    }

}
