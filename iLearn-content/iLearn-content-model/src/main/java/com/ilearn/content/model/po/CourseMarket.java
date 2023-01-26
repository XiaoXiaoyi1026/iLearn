package com.ilearn.content.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 课程营销信息
 * </p>
 *
 * @author xiaoxiaoyi
 */
@Data
@TableName("course_market")
public class CourseMarket implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，课程id
     */
    @ApiModelProperty("课程id")
    private Long id;

    /**
     * 收费规则，对应数据字典
     */
    @ApiModelProperty("收费规则")
    private String charge;

    /**
     * 现价
     */
    @ApiModelProperty("现价")
    private Float price;

    /**
     * 原价
     */
    @ApiModelProperty("原价")
    private Float originalPrice;

    /**
     * 咨询qq
     */
    @ApiModelProperty("咨询qq")
    private String qq;

    /**
     * 微信
     */
    @ApiModelProperty("微信")
    private String wechat;

    /**
     * 电话
     */
    @ApiModelProperty("电话")
    private String phone;

    /**
     * 有效期天数
     */
    @ApiModelProperty("有效期天数")
    private Integer validDays;


}
