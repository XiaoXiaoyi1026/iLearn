package com.ilearn.base.dictionary;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 订单状态
 * @date 1/27/2023 8:00 PM
 */
public class OrderStatus {

    /**
     * 未支付
     */
    public static final String NON_PAYMENT = "600001";

    /**
     * 已支付
     */
    public static final String PAID = "600002";

    /**
     * 已关闭
     */
    public static final String CLOSED = "600003";

    /**
     * 已退款
     */
    public static final String REFUNDED = "600004";

    /**
     * 已完成
     */
    public static final String COMPLETED = "600005";

}
