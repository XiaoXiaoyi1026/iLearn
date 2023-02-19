package com.ilearn.task.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ilearn.task.mapper.MqMessageHistoryMapper;
import com.ilearn.task.model.po.MqMessageHistory;
import com.ilearn.task.service.MqMessageHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xiaoxiaoyi
 */
@Slf4j
@Service
public class MqMessageHistoryServiceImpl extends ServiceImpl<MqMessageHistoryMapper, MqMessageHistory> implements MqMessageHistoryService {

}
