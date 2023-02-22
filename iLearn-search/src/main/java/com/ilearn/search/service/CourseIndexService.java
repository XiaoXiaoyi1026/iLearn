package com.ilearn.search.service;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程索引服务
 * @date 2/22/2023 4:05 PM
 */
public interface CourseIndexService {

    /**
     * 添加课程索引(elasticsearch索引库文档)
     *
     * @param indexName 索引库名称
     * @param id        文档id
     * @param object    文档对应的对象
     * @return 添加成功与否
     */
    Boolean addCourseIndex(String indexName, String id, Object object);

}
