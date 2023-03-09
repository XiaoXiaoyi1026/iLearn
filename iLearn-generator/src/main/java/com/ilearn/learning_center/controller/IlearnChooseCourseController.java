package com.ilearn.learning_center.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ilearn.learning_center.service.IlearnChooseCourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaoxiaoyi
 */
@Slf4j
@RestController
@RequestMapping("ilearnChooseCourse")
public class IlearnChooseCourseController {

    @Autowired
    private IlearnChooseCourseService  ilearnChooseCourseService;
}