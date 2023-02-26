package com.ilearn.users;

import com.ilearn.users.model.dto.AuthorizeInfo;
import com.ilearn.users.service.AuthorizeService;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 授权(策略模式)
 * @date 2/26/2023 4:32 PM
 */
public class Authorize {

    private final AuthorizeService authorizeService;

    public Authorize(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService;
    }

    public UserDetails execute(AuthorizeInfo authorizeInfo) {
        return authorizeService.execute(authorizeInfo);
    }

}
