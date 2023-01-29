package com.ilearn.base.exception;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description JSR303校验分组
 * @date 1/29/2023 10:49 AM
 */
public class ValidationGroups {

    /**
     * 添加参数校验
     */
    public interface Insert{}

    /**
     * 更新参数校验
     */
    public interface Update{}

    /**
     * 删除参数校验
     */
    public interface Delete{}

}
