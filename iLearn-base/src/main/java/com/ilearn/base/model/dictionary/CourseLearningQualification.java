package com.ilearn.base.model.dictionary;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程学习资格
 * @date 1/27/2023 8:30 PM
 */
public final class CourseLearningQualification {

    /**
     * 正常学习
     */
    public static final String NORMAL = "702001";

    /**
     * 异常, 未选课/选课后未支付
     */
    public static final String EXCEPTION = "702002";

    /**
     * 已过期需要申请续约或重新支付
     */
    public static final String EXPIRED = "702003";

}
