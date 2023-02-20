package com.ilearn.content;

import com.ilearn.content.model.dto.CoursePreviewDto;
import com.ilearn.content.service.CoursePublishService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description freemarker测试
 * @date 2/19/2023 3:35 PM
 */
@SpringBootTest()
public class FreemarkerTest {

    @Autowired
    private CoursePublishService coursePublishService;

    @Test
    public void testCoursePublish() throws IOException, TemplateException {
        // 配置freemarker
        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        // 得到classpath路径
        String classpath = Objects.requireNonNull(this.getClass().getResource("/")).getPath();
        // 设置模板文件的生成路径
        configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
        // 设置字符集
        configuration.setDefaultEncoding("utf-8");
        // 指定模板文件的名称
        Template courseTemplate = configuration.getTemplate("course_template.ftl");
        // 准备数据
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(18L);
        Map<String, Object> data = new HashMap<>();
        data.put("model", coursePreviewInfo);
        // 生成静态页面
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(courseTemplate, data);
        System.out.println(content);
        // 将静态化数据输出到文件中
        InputStream inputStream = IOUtils.toInputStream(content);
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\Download\\Java\\SpringCloud\\iLearn\\iLearn\\iLearn-content\\iLearn-content-service\\src\\test\\resources\\templates\\test.html");
        IOUtils.copy(inputStream, fileOutputStream);
        fileOutputStream.close();
    }

}
