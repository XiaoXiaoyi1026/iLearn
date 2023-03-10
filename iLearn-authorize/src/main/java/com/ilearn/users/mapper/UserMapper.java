package com.ilearn.users.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ilearn.base.model.po.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author xiaoxiaoyi
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
