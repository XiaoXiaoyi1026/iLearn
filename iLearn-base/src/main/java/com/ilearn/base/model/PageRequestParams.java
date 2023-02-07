package com.ilearn.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 分页查询通用请求参数
 * @date 1/24/2023 4:44 PM
 */
@Data
@ToString
public class PageRequestParams {

    //当前页码默认值
    public static final long DEFAULT_PAGE_CURRENT = 1L;
    //每页记录数默认值
    public static final long DEFAULT_PAGE_SIZE = 10L;

    //当前页码
    @ApiModelProperty("当前页码")
    private Long pageNo = DEFAULT_PAGE_CURRENT;

    //每页记录数默认值
    @ApiModelProperty("每页记录数")
    private Long pageSize = DEFAULT_PAGE_SIZE;

    public PageRequestParams() {

    }

    public PageRequestParams(long pageNo, long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

}
