package com.ilearn.users.model.po;

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
@TableName("ilearn_teacher")
public class IlearnTeacher implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 称呼
     */
    private String name;

    /**
     * 个人简介
     */
    private String intro;

    /**
     * 个人简历
     */
    private String resume;

    /**
     * 老师照片
     */
    private String pic;


}
