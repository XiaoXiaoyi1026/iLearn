package com.ilearn.content.api;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 由于freemarker返回的不是json, 而是静态html页面, 所以不用RestController
 *
 * @author xiaoxiaoyi
 * @version 1.0
 * @description freemarker模板的访问控制器
 * @date 2/14/2023 3:11 PM
 */
@Controller
@Api(value = "freemarker访问控制器")
@RequestMapping("/freemarker")
public class FreemarkerController {

    /**
     * 测试freemarker
     *
     * @return 模型视图
     */
    @GetMapping("/test")
    public ModelAndView test() {
        ModelAndView modelAndView = new ModelAndView();
        // 指定视图名称, 对应ftl文件的名称
        modelAndView.setViewName("test");
        // 准备模型数据
        modelAndView.addObject("name", "xiaoxiaoyi");
        return modelAndView;
    }

}
