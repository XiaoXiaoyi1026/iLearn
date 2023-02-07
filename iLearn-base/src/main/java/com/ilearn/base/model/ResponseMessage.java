package com.ilearn.base.model;

import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 通用响应结果
 * @date 2023/2/6 17:26
 */

@Data
@ToString
public class ResponseMessage<T> {

    /**
     * 响应编码,0为正常,-1错误
     */
    private int code;

    /**
     * 响应提示信息
     */
    private String msg;

    /**
     * 响应内容
     */
    private T result;


    public ResponseMessage() {
        this(0, "success");
    }

    public ResponseMessage(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 错误信息的封装
     *
     * @param msg 响应信息
     * @param <T> 对象类型
     * @return Rest请求响应
     */
    public static <T> @NotNull ResponseMessage<T> validFail(String msg) {
        ResponseMessage<T> response = new ResponseMessage<>();
        response.setCode(-1);
        response.setMsg(msg);
        return response;
    }

    /**
     * @param result 响应的对象
     * @param msg    响应信息
     * @param <T>    对象类型
     * @return Rest请求响应
     */
    public static <T> @NotNull ResponseMessage<T> validFail(T result, String msg) {
        ResponseMessage<T> response = new ResponseMessage<>();
        response.setCode(-1);
        response.setResult(result);
        response.setMsg(msg);
        return response;
    }


    /**
     * 添加正常响应数据（包含响应内容）
     *
     * @return ResponseMessage Rest服务封装相应数据
     */
    public static <T> @NotNull ResponseMessage<T> success(T result) {
        ResponseMessage<T> response = new ResponseMessage<>();
        response.setResult(result);
        return response;
    }

    public static <T> @NotNull ResponseMessage<T> success(T result, String msg) {
        ResponseMessage<T> response = new ResponseMessage<>();
        response.setResult(result);
        response.setMsg(msg);
        return response;
    }

    /**
     * 添加正常响应数据（不包含响应内容）
     *
     * @return ResponseMessage Rest服务封装相应数据
     */
    @Contract(value = " -> new", pure = true)
    public static <T> @NotNull ResponseMessage<T> success() {
        return new ResponseMessage<>();
    }


    public Boolean isSuccessful() {
        return this.code == 0;
    }

}