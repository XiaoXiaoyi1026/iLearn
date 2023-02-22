package com.ilearn.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.ilearn.base.exception.ILearnException;
import com.ilearn.search.service.CourseIndexService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程索引服务实现类
 * @date 2/22/2023 4:07 PM
 */
@Slf4j
@Service
public class CourseIndexServiceImpl implements CourseIndexService {

    private RestHighLevelClient restHighLevelClient;

    @Autowired
    void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public Boolean addCourseIndex(String indexName, String id, Object object) {
        String jsonString = JSON.toJSONString(object);
        IndexRequest indexRequest = new IndexRequest(indexName).id(id);
        //指定索引文档内容
        indexRequest.source(jsonString, XContentType.JSON);
        //索引响应对象
        IndexResponse indexResponse = null;
        try {
            indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("添加索引出错:{}", e.getMessage());
            e.printStackTrace();
            ILearnException.cast("添加索引出错");
        }
        String name = indexResponse.getResult().name();
        System.out.println(name);
        return name.equalsIgnoreCase("created") || name.equalsIgnoreCase("updated");
    }
}
