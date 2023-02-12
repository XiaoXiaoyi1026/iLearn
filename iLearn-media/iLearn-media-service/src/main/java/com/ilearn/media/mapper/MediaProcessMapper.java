package com.ilearn.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ilearn.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author xiaoxiaoyi
 */
@Mapper
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    /**
     * 根据分片和核心线程数决定要处理的视频信息列表
     *
     * @param shardTotal 分片总数
     * @param shardIndex 当前分片编号
     * @param count      用于处理视频的线程数, 一般等于CPU的核心线程数
     * @return 根据分片进行选择的要处理的视频信息列表
     */
    @Select("SELECT * FROM media_process WHERE id % #{shardTotal} = #{shardIndex} AND status = '1' LIMIT #{count}")
    List<MediaProcess> selectByShard(@Param("shardTotal") int shardTotal, @Param("shardIndex") int shardIndex, @Param("count") int count);
}