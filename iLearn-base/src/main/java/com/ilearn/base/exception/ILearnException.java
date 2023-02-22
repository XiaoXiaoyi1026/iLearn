package com.ilearn.base.exception;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 通用的异常类
 * @date 1/28/2023 4:34 PM
 */
public class ILearnException extends RuntimeException {

    private final String message;

    public ILearnException(String message) {
        super(message);
        this.message = message;
    }

    public static void cast(String message) {
        throw new ILearnException(message);
    }

    @Contract("_ -> fail")
    public static void cast(@NotNull CommonError commonError) {
        throw new ILearnException(commonError.getErrMessage());
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
