package com.ilearn.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.ilearn.base.model.PageRequestParams;
import com.ilearn.search.model.CourseSearchParamsDto;
import com.ilearn.search.model.CourseSearchResultDto;
import com.ilearn.search.model.CourseIndex;
import com.ilearn.search.service.CourseSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程搜索实现类
 * @date 2/22/2023 4:54 PM
 */
@Slf4j
@Service
public class CourseSearchServiceImpl implements CourseSearchService {

    @Value("${elasticsearch.course.index}")
    private String courseIndexStore;
    @Value("${elasticsearch.course.source_fields}")
    private String sourceFields;

    private RestHighLevelClient restHighLevelClient;

    @Autowired
    void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public CourseSearchResultDto<CourseIndex> queryCoursePubIndex(@NotNull PageRequestParams pageRequestParams, CourseSearchParamsDto courseSearchParamsDto) {
        //设置索引
        SearchRequest searchRequest = new SearchRequest(courseIndexStore);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //source源字段过虑
        String[] sourceFieldsArray = sourceFields.split(",");
        searchSourceBuilder.fetchSource(sourceFieldsArray, new String[]{});
        //分页
        Long pageNo = pageRequestParams.getPageNo();
        Long pageSize = pageRequestParams.getPageSize();
        int start = (int) ((pageNo - 1) * pageSize);
        searchSourceBuilder.from(start);
        searchSourceBuilder.size(Math.toIntExact(pageSize));
        //布尔查询
        searchSourceBuilder.query(boolQueryBuilder);
        //请求搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("课程搜索异常：{}", e.getMessage());
            return new CourseSearchResultDto<>(new ArrayList<>(), 0, 0, 0);
        }
        //结果集处理
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        //记录总数
        TotalHits totalHits = hits.getTotalHits();
        //数据列表
        List<CourseIndex> list = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            String sourceAsString = hit.getSourceAsString();
            CourseIndex courseIndex = JSON.parseObject(sourceAsString, CourseIndex.class);
            list.add(courseIndex);
        }
        return new CourseSearchResultDto<>(list, totalHits.value, pageNo, pageSize);
    }
}
