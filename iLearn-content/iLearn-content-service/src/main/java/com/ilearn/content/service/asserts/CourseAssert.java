package com.ilearn.content.service.asserts;

import com.ilearn.base.exception.ILearnException;
import com.ilearn.content.model.po.CourseBase;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程通用校验
 * @date 2/18/2023 2:58 PM
 */
@Slf4j
public class CourseAssert {

    /**
     * 判断课程是否存在
     *
     * @param courseBase 课程信息
     * @param message    错误信息
     */
    public static void notNull(CourseBase courseBase, String message) {
        if (courseBase == null) {
            log.error("{}", message);
            ILearnException.cast(message);
        }
    }

    /**
     * 判断课程是否存在
     *
     * @param courseBase 课程信息
     */
    public static void notNull(CourseBase courseBase) {
        notNull(courseBase, "课程不存在");
    }

    /**
     * 判断课程能否操作
     *
     * @param companyId  机构id
     * @param courseBase 课程信息
     */
    public static void companyIdValid(Long companyId, @NotNull CourseBase courseBase) {
        companyIdValid(companyId, courseBase, "不能操作不属于自己机构的课程");
    }

    /**
     * 判断课程能否操作
     *
     * @param companyId  机构id
     * @param courseBase 课程信息
     * @param message    错误信息
     */
    public static void companyIdValid(@NotNull Long companyId, @NotNull CourseBase courseBase, String message) {
        Long courseCompanyId = courseBase.getCompanyId();
        if (!companyId.equals(courseCompanyId)) {
            log.error("{}, courseCompanyId: {}, companyId: {}", message, courseCompanyId, companyId);
            ILearnException.cast(message);
        }
    }

    /**
     * 判断课程审核状态
     *
     * @param courseAuditStatus 审核状态
     * @param courseBase        课程信息
     */
    public void auditStatusValid(@NotNull String courseAuditStatus, @NotNull CourseBase courseBase) {
        auditStatusValid(courseAuditStatus, courseBase, "审核状态异常");
    }

    /**
     * 判断课程审核状态
     *
     * @param courseAuditStatus 审核状态
     * @param courseBase        课程信息
     * @param message           错误提示信息
     */
    public static void auditStatusValid(@NotNull String courseAuditStatus, @NotNull CourseBase courseBase, String message) {
        if (!courseAuditStatus.equals(courseBase.getAuditStatus())) {
            log.error("{}, courseAuditStatus: {}, courseBase: {}", message, courseAuditStatus, courseBase);
            ILearnException.cast(message);
        }
    }
}
