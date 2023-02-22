package com.ilearn.base.exception;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 通用异常
 * @date 1/28/2023 4:43 PM
 */
public enum CommonError {

    UNKNOWN_ERROR("执行过程异常，请重试。"),
    PARAMS_ERROR("非法参数"),
    OBJECT_NULL("对象为空"),
    QUERY_NULL("查询结果为空"),
    REQUEST_NULL("请求参数为空");

    private final String errMessage;

    public String getErrMessage() {
        return errMessage;
    }

    CommonError(String errMessage) {
        this.errMessage = errMessage;
    }

}
