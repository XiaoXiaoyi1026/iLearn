package com.ilearn.search.model.dto;

import com.ilearn.base.model.PageResponse;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程搜索返回结果
 * @date 2/22/2023 4:43 PM
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@ApiModel(value = "课程搜索返回结果", description = "课程搜索的结果")
public class CourseSearchResultDto<T> extends PageResponse<T> {

    /**
     * @param items    页面对象集合
     * @param counts   对象总数量
     * @param page     页码
     * @param pageSize 每页存放的对象数
     */
    public CourseSearchResultDto(List<T> items, long counts, long page, long pageSize) {
        super(items, counts, page, pageSize);
    }

}
