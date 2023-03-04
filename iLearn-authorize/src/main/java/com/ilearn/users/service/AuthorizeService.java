package com.ilearn.users.service;

import com.ilearn.users.model.dto.AuthorizeInfo;
import com.ilearn.users.model.dto.ILearnUserAuthorities;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 授权服务接口
 * @date 2/26/2023 4:24 PM
 */
public interface AuthorizeService {

    /**
     * 验证方法, 由具体的每个验证实现类其自己实现
     *
     * @param authorizeInfo 授权信息
     * @return 用户详情
     */
    ILearnUserAuthorities execute(AuthorizeInfo authorizeInfo);

}
