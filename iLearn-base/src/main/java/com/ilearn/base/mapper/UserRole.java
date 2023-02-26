package com.ilearn.base.mapper;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 用户角色
 * @date 2/26/2023 11:02 AM
 */
public enum UserRole {

    /**
     * 学生
     */
    STUDENT,

    /**
     * 老师
     */
    TEACHER,

    /**
     * 教学管理员
     */
    TEACH_MANAGER,

    /**
     * 管理员
     */
    ADMIN,

    /**
     * 超级管理员
     */
    SUPER_ADMIN;

    @Contract(pure = true)
    public static @Nullable UserRole getUserRole(@NotNull String userRoleCode) {
        switch (userRoleCode) {
            case UserRoleCode.STUDENT:
                return STUDENT;
            case UserRoleCode.TEACHER:
                return TEACHER;
            case UserRoleCode.TEACH_MANAGER:
                return TEACH_MANAGER;
            case UserRoleCode.ADMIN:
                return ADMIN;
            case UserRoleCode.SUPER_ADMIN:
                return SUPER_ADMIN;
            default:
                return null;
        }
    }

}
