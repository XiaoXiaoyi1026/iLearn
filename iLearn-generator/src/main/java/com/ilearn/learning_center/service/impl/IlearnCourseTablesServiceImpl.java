package com.ilearn.learning_center.service.impl;

import com.ilearn.learning_center.model.po.IlearnCourseTables;
import com.ilearn.learning_center.mapper.IlearnCourseTablesMapper;
import com.ilearn.learning_center.service.IlearnCourseTablesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaoxiaoyi
 */
@Slf4j
@Service
public class IlearnCourseTablesServiceImpl extends ServiceImpl<IlearnCourseTablesMapper, IlearnCourseTables> implements IlearnCourseTablesService {

}
