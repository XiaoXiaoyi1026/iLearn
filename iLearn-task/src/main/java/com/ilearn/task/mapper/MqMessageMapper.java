package com.ilearn.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ilearn.task.model.po.MqMessage;
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
public interface MqMessageMapper extends BaseMapper<MqMessage> {

    @Select("SELECT t.* FROM mq_message t WHERE t.id % #{shardTotal} = #{shardIndex} and t.state='0' and t.message_type=#{messageType} limit #{count}")
    List<MqMessage> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardIndex") int shardIndex, @Param("messageType") String messageType, @Param("count") int count);

}
