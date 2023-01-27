package com.ilearn.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ilearn.content.model.dto.CourseCategoryDto;
import com.ilearn.content.model.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author xiaoxiaoyi
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    /**
     * @param id 树型分类的根节点
     * @return 该节点下的所有分类
     */
    List<CourseCategoryDto> selectTreeNodes(String id);

}
