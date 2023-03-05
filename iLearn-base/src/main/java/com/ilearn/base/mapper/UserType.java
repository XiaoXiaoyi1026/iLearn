package com.ilearn.base.mapper;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 用户类型代码
 * @date 3/5/2023 3:21 PM
 */
public class UserType {

    @Contract(pure = true)
    public static @Nullable String get(@NotNull UserRole userRole) {
        switch (userRole) {
            case STUDENT:
                return "101001";
            case TEACHER:
                return "101002";
            case TEACH_MANAGER:
                return "101003";
            case ADMIN:
                return "101004";
            case SUPER_ADMIN:
                return "101005";
            default:
                return null;
        }
    }

}
