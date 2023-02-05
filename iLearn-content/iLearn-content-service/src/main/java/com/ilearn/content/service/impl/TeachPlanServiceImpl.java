package com.ilearn.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ilearn.base.exception.ILearnException;
import com.ilearn.content.mapper.TeachPlanMapper;
import com.ilearn.content.model.dto.SaveTeachPlanDto;
import com.ilearn.content.model.dto.TeachPlanDto;
import com.ilearn.content.model.po.TeachPlan;
import com.ilearn.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 教学计划服务实现类
 * @date 1/31/2023 2:11 PM
 */
@Service
@Slf4j
public class TeachPlanServiceImpl implements TeachPlanService {

    private final TeachPlanMapper teachPlanMapper;

    @Autowired
    TeachPlanServiceImpl(TeachPlanMapper teachPlanMapper) {
        this.teachPlanMapper = teachPlanMapper;
    }

    @Override
    public List<TeachPlanDto> getTreeNodes(Long courseId) {
        return teachPlanMapper.getTreeNodes(courseId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveTeachPlan(@NotNull SaveTeachPlanDto saveTeachPlanDto) {
        // 根据传入参数中是否教学计划id判断是新增还是修改
        Long teachPlanId = saveTeachPlanDto.getId();
        TeachPlan teachPlan = new TeachPlan();
        LocalDateTime now = LocalDateTime.now();
        teachPlan.setChangeDate(now);
        if (teachPlanId == null) {
            // 如果为空, 则是新增, 设置创建时间
            teachPlan.setCreateDate(now);
            // 设置时长
            this.setTimeLength(teachPlan);
            BeanUtils.copyProperties(saveTeachPlanDto, teachPlan);
            // 计算新插入的教学计划顺序orderBy, 默认应该排在最后
            teachPlan.setOrderby(this.getOrderBy(teachPlan));
            teachPlanMapper.insert(teachPlan);
        } else {
            // 不为空则是修改, 先查出之前的
            teachPlan = teachPlanMapper.selectById(teachPlanId);
            if (teachPlan == null) {
                ILearnException.cast("修改教学计划失败!数据库中不存在该教学计划!");
            }
            if (saveTeachPlanDto.getCoursePubId() != null && !saveTeachPlanDto.getCoursePubId().equals(teachPlan.getCoursePubId())) {
                ILearnException.cast("课程发布状态不一致, 不可修改!");
            }
            // 再将更新信息赋值给之前的
            BeanUtils.copyProperties(saveTeachPlanDto, teachPlan);
            // 设置时间
            this.setTimeLength(teachPlan);
            // 把之前的保存回去
            teachPlanMapper.updateById(teachPlan);
        }
    }

    private void setTimeLength(@NotNull TeachPlan teachPlan) {
        LocalDateTime startTime = teachPlan.getStartTime();
        LocalDateTime endTime = teachPlan.getEndTime();
        if (startTime == null) {
            if (endTime != null) {
                ILearnException.cast("请设置开始时间!");
            } else {
                teachPlan.setTimelength("未设置");
                return;
            }
        }
        if (endTime == null) {
            teachPlan.setTimelength("未设置");
            return;
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            ILearnException.cast("起始时间不能在当前时间之前!");
        }
        // 计算时长
        if (startTime.isAfter(endTime)) {
            ILearnException.cast("起始时间不能晚于结束时间!");
        }
        StringBuilder timeLength = new StringBuilder();
        // 求出开始时间到结束时间之间的时长的秒数
        int seconds = (int) (Duration.between(startTime, endTime).toMillis() / 1000);
        // 计算秒数对应的分钟数
        int minutes = seconds / 60;
        if (minutes < 10) {
            ILearnException.cast("时长不能小于10分钟!");
        }
        // 计算小时数
        int hours = minutes / 60;
        this.appendTime(timeLength, hours);
        timeLength.append(':');
        // 更新分钟数
        minutes %= 60;
        this.appendTime(timeLength, minutes);
        timeLength.append(':');
        // 更新秒数
        seconds %= 60;
        this.appendTime(timeLength, seconds);
        teachPlan.setTimelength(String.valueOf(timeLength));
    }

    /**
     * 获取教学计划的orderBy
     *
     * @param teachPlan 教学计划
     * @return 排序字段值
     */
    private int getOrderBy(@NotNull TeachPlan teachPlan) {
        if (teachPlan.getOrderby() != null) {
            return teachPlan.getOrderby();
        }
        // 要先根据courseId和parentId查出和它同级的数量
        LambdaQueryWrapper<TeachPlan> teachPlanLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Long courseId = teachPlan.getCourseId();
        if (courseId == null) {
            ILearnException.cast("教学计划id为空, 获取计划排序位置失败!");
        }
        Long parentId = teachPlan.getParentid();
        if (parentId == null) {
            ILearnException.cast("教学计划parentId为空, 获取计划排序位置失败!");
        }
        // 构造查询条件
        teachPlanLambdaQueryWrapper.eq(TeachPlan::getCourseId, courseId).eq(TeachPlan::getParentid, parentId);
        // 查询所有同级的
        List<TeachPlan> sameLayerTeachPlans = teachPlanMapper.selectList(teachPlanLambdaQueryWrapper);
        // 获取同组中orderBy最大的那个
        int result = Integer.MIN_VALUE;
        for (TeachPlan sameLayerTeachPlan : sameLayerTeachPlans) {
            result = Math.max(result, sameLayerTeachPlan.getOrderby());
        }
        // 要排到最后就要比最大的orderBy还大
        return result + 1;
    }

    private void appendTime(StringBuilder timeLength, int length) {
        if (length < 10) {
            timeLength.append(0);
        }
        timeLength.append(length);
    }
}
