package com.ilearn.users.mapper;

import com.ilearn.users.model.po.IlearnRole;
import com.ilearn.users.model.po.IlearnUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author xiaoxiaoyi
 */
@Mapper
public interface IlearnUserMapper extends BaseMapper<IlearnUser> {

    /**
     * 根据用户id获取角色代码
     *
     * @param userId 用户id
     * @return 角色代码
     */
    @Select("SELECT * FROM ilearn_users.ilearn_role WHERE id = (SELECT role_id FROM ilearn_users.ilearn_user_role WHERE user_id = ${userId})")
    IlearnRole getRole(String userId);

}
