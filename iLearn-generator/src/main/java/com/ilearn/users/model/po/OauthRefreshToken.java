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
@TableName("oauth_refresh_token")
public class OauthRefreshToken implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tokenId;

    private Blob token;

    private Blob authentication;


}
