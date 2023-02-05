package com.ilearn.system.api;

import com.ilearn.system.model.po.Dictionary;
import com.ilearn.system.service.DictionaryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xiaoxiaoyi
 * <p>
 * 数据字典 前端控制器
 * </p>
 * 跨域问题的3种解决方案:
 * 1. JSONP: 前端通过<script src="http://跨域地址:端口?callback="回调方法名"></script>"></script>的src属性携带callback参数执行跨域请求
 * 后端接收到请求先读取callback参数得到回调方法名, 然后将结果封装为js代码响应给前端
 * 前端收到响应执行该js代码对请求结果进行处理即可
 * 2. 服务端在请求头添加Access-Control-Allow-Origin: *
 * 也可在Controller上使用@CrossOrigin(origins = "*")注解可以解决跨域问题
 * 3. 使用反向代理服务进行跨域请求:
 * 前端不知接请求跨域服务, 而是请求与自己同源的反向代理服务器
 * 反向代理服务器收到请求后由反向代理服务器去执行跨域请求, 由于同源策略是由浏览器维护的
 * 而服务与服务间的请求并不会经过浏览器, 所以不会产生跨域问题
 */
@Slf4j
@RestController
@Api(value = "数据字典接口", tags = "数据字典数据管理")
// @CrossOrigin(origins = {"http://localhost:8601"})
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    @GetMapping("/dictionary/all")
    public List<Dictionary> queryAll() {
        return dictionaryService.queryAll();
    }

    @GetMapping("/dictionary/code/{code}")
    public Dictionary getByCode(@PathVariable String code) {
        return dictionaryService.getByCode(code);
    }
}
