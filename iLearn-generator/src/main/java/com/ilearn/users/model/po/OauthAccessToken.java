package com.ilearn.users.model.po;

import java.sql.Blob;
import java.io.Serializable;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaoxiaoyi
 */
@Data
@TableName("oauth_access_token")
public class OauthAccessToken implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tokenId;

    private Blob token;

    private String authenticationId;

    private String userName;

    private String clientId;

    private Blob authentication;

    private String refreshToken;


}
