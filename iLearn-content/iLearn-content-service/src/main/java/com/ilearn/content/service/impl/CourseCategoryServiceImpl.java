package com.ilearn.content.service.impl;

import com.ilearn.content.mapper.CourseCategoryMapper;
import com.ilearn.content.model.dto.CourseCategoryDto;
import com.ilearn.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 课程分类相关接口实现
 * @date 1/27/2023 1:26 PM
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryDto> queryCourseCategory(String id) {
        // 最终需要返回的list
        List<CourseCategoryDto> result = new ArrayList<>();
        if (id != null) {
            // 得到了根节点下的所有子节点
            List<CourseCategoryDto> courseCategoryDtoList = courseCategoryMapper.selectTreeNodes(id);
            // 为了方便查找一个节点的父节点, 使用map存储每一个节点
            Map<String, CourseCategoryDto> courseCategoryDtoMap = new HashMap<>();
            // 将根节点的直接下属节点封装到根节点的childrenTreeNodes中
            courseCategoryDtoList.forEach(
                    courseCategoryDto -> {
                        courseCategoryDtoMap.put(courseCategoryDto.getId(), courseCategoryDto);
                        // 找到子节点并放入它的父节点的childrenTreeNodes中
                        String courseCategoryParentDtoId = courseCategoryDto.getParentid();
                        // 去map里找当前节点的父节点
                        CourseCategoryDto courseCategoryParentDto = courseCategoryDtoMap.get(courseCategoryParentDtoId);
                        // 判断父节点是否存在
                        if (courseCategoryParentDto != null) {
                            // 如果父节点存在, 则拿出父节点的childrenTreeNodes
                            List<CourseCategoryDto> childrenTreeNodes = courseCategoryParentDto.getChildrenTreeNodes();
                            if (childrenTreeNodes == null) {
                                courseCategoryParentDto.setChildrenTreeNodes(new ArrayList<>());
                                childrenTreeNodes = courseCategoryParentDto.getChildrenTreeNodes();
                            }
                            // 将当前节点加入到它父节点的childrenTreeNodes中
                            childrenTreeNodes.add(courseCategoryDto);
                        }
                        if (id.equals(courseCategoryParentDtoId)) {
                            result.add(courseCategoryDto);
                        }
                    }
            );
        }
        // 返回的list中, 只包含了根节点的直接下属节点
        return result;
    }
}
