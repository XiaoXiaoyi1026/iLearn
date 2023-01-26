package com.ilearn.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description TODO 分页查询通用响应参数
 * @date 1/24/2023 4:56 PM
 */
@Data
@ToString
public class PageResult<T> {

    // 数据列表
    @ApiModelProperty("数据列表")
    private List<T> items;

    // 总记录数
    @ApiModelProperty("总记录数")
    private long counts;

    // 当前页码
    @ApiModelProperty("当前页码")
    private long page;

    // 每页记录数
    @ApiModelProperty("每页记录数")
    private long pageSize;

    public PageResult() {

    }

    public PageResult(List<T> items, long counts, long page, long pageSize) {
        this.items = items;
        this.counts = counts;
        this.page = page;
        this.pageSize = pageSize;
    }

}
