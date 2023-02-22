package com.ilearn.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;


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
    public RestErrorResponse iLearnExceptionHandler(@NotNull ILearnException iLearnException) {
        String message = iLearnException.getMessage();
        // 记录日志信息
        log.error("捕获到异常: {}", message);
        iLearnException.printStackTrace();
        return new RestErrorResponse(message);
    }

    /**
     * 处理由JSR303校验出的MethodArgumentNotValidException
     * 该异常的响应状态码约定为HttpStatus.INTERNAL_SERVER_ERROR(500)
     * 约定返回的信息数据格式为JSON(@ResponseBody)
     *
     * @param methodArgumentNotValidException JSR303校验抛出的参数值异常
     * @return 与前端约定好的异常数据格式
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidExceptionHandler(@NotNull MethodArgumentNotValidException methodArgumentNotValidException) {
        // 取出所有异常信息, 假设表单有多个参数异常, 那么都能取到
        BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
        // 获取参数错误信息
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        // 收集错误信息
        StringBuilder errors = new StringBuilder();
        // 遍历取出异常信息
        int n = fieldErrors.size();
        for (int i = 0; i < n - 1; i++) {
            errors.append(fieldErrors.get(i).getDefaultMessage()).append('&');
        }
        errors.append(fieldErrors.get(n - 1).getDefaultMessage());
        // 记录日志信息
        log.error("JSR303校验捕获到参数值异常: {}", errors);
        methodArgumentNotValidException.printStackTrace();
        return new RestErrorResponse(String.valueOf(errors));
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
    public RestErrorResponse exceptionHandler(@NotNull Exception exception) {
        // 记录日志信息
        log.error("捕获到预期之外的异常: {}", exception.getMessage());
        exception.printStackTrace();
        // 返回给前端比较友好的信息
        return new RestErrorResponse(CommonError.UNKNOWN_ERROR.getErrMessage());
    }

}
