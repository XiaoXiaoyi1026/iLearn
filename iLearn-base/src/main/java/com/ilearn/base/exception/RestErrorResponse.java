package com.ilearn.base.exception;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 和前端约定好的如果发生异常将要返回的异常对象(JSON)
 * @date 1/28/2023 4:59 PM
 */
public class RestErrorResponse {

    private final String errMessage;

    public RestErrorResponse(String message) {
        this.errMessage = message;
    }

    public String getErrMessage() {
        return this.errMessage;
    }

}
