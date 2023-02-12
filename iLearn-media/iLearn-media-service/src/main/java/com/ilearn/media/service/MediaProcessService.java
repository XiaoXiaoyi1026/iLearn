package com.ilearn.media.service;

import com.ilearn.media.model.po.MediaProcess;

import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 媒体处理服务
 * @date 2/12/2023 4:53 PM
 */
public interface MediaProcessService {

    /**
     * 根据分片情况, 即处理器情况得到处理器要处理的数据
     *
     * @param shardTotal 分片总数
     * @param shardIndex 当前处理器的分片编号
     * @param count      CPU的核心线程数
     * @return 当期处理器要处理的media列表
     */
    List<MediaProcess> getMediaProcessList(int shardTotal, int shardIndex, int count);

}
