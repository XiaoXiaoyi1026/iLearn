package com.ilearn.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 通用异常处理器
 * @date 1/28/2023 4:49 PM
 * ControllerAdvice: 控制器(Controller)增强
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理程序中主动抛出的可预知的ILearnException
     * 该异常的响应状态码约定为HttpStatus.INTERNAL_SERVER_ERROR(500)
     * 约定返回的信息数据格式为JSON(@ResponseBody)
     *
     * @param iLearnException 可预知异常
     * @return 与前端约定好的异常数据格式
     */
    @ResponseBody
    @ExceptionHandler(ILearnException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse iLearnExceptionHandler(ILearnException iLearnException) {
        String message = iLearnException.getMessage();
        // 记录日志信息
        log.error("捕获到异常: {}", message);
        iLearnException.printStackTrace();
        return new RestErrorResponse(message);
    }

    /**
     * 处理程序中不可预知的Exception
     * 该异常的响应状态码约定为HttpStatus.INTERNAL_SERVER_ERROR(500)
     * 约定返回的信息数据格式为JSON(@ResponseBody)
     *
     * @param exception 不可预知异常
     * @return 与前端约定好的异常数据格式
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exceptionHandler(Exception exception) {
        // 记录日志信息
        log.error("捕获到预期之外的异常: {}", exception.getMessage());
        exception.printStackTrace();
        // 返回给前端比较友好的信息
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }

}
