package com.ilearn.media.service.impl;

import com.ilearn.media.mapper.MediaProcessMapper;
import com.ilearn.media.model.po.MediaProcess;
import com.ilearn.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 媒体处理服务实现
 * @date 2/12/2023 5:02 PM
 */
@Slf4j
@Service
public class MediaProcessServiceImpl implements MediaProcessService {

    private MediaProcessMapper mediaProcessMapper;

    @Autowired
    void setMediaProcessMapper(MediaProcessMapper mediaProcessMapper) {
        this.mediaProcessMapper = mediaProcessMapper;
    }

    @Override
    public List<MediaProcess> getMediaProcessList(int shardTotal, int shardIndex, int count) {
        return mediaProcessMapper.selectByShard(shardTotal, shardIndex, count);
    }
}
