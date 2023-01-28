package com.ilearn.base.exception;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 通用的异常类
 * @date 1/28/2023 4:34 PM
 */
public class ILearnException extends RuntimeException {

    private String message;

    public ILearnException() {
        super();
    }

    public ILearnException(String message) {
        super(message);
        this.message = message;
    }

    public static void cast(String message) {
        throw new ILearnException(message);
    }

    public static void cast(CommonError commonError) {
        throw new ILearnException(commonError.getErrMessage());
    }

    public String getMessage() {
        return this.message;
    }
}
