package com.ilearn.content.service.impl;

import com.ilearn.content.mapper.TeachPlanMapper;
import com.ilearn.content.model.dto.TeachPlanDto;
import com.ilearn.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private TeachPlanMapper teachPlanMapper;

    @Override
    public List<TeachPlanDto> getTreeNodes(Long courseId) {
        return teachPlanMapper.getTreeNodes(courseId);
    }
}
