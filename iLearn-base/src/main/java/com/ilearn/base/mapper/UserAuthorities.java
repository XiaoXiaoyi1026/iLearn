package com.ilearn.base.mapper;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 用户权限
 * @date 2/26/2023 11:09 AM
 */
public class UserAuthorities {

    private static final String TEST = "test";

    @Contract(pure = true)
    public static String @NotNull [] getUserAuthorities(@NotNull UserRole userRole) {
        switch (userRole) {
            case STUDENT:
                return new String[]{TEST};
            case TEACHER:
                return new String[]{TEST};
            case ADMIN:
                return new String[]{TEST};
            case SUPER_ADMIN:
                return new String[]{TEST};
            case TEACH_MANAGER:
                return new String[]{TEST};
            default:
                return new String[]{TEST};
        }
    }

}
