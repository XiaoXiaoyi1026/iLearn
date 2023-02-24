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
@TableName("oauth_code")
public class OauthCode implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    private Blob authentication;


}
