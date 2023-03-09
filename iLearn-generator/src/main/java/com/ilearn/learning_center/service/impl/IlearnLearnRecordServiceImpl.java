package com.ilearn.learning_center.service.impl;

import com.ilearn.learning_center.model.po.IlearnLearnRecord;
import com.ilearn.learning_center.mapper.IlearnLearnRecordMapper;
import com.ilearn.learning_center.service.IlearnLearnRecordService;
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
public class IlearnLearnRecordServiceImpl extends ServiceImpl<IlearnLearnRecordMapper, IlearnLearnRecord> implements IlearnLearnRecordService {

}
