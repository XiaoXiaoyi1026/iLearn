package com.ilearn.users.mapper;

import com.ilearn.users.model.po.IlearnMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaoxiaoyi
 */
@Mapper
public interface IlearnMenuMapper extends BaseMapper<IlearnMenu> {

    /**
     * 根据用户id查询对应权限
     *
     * @param userID 用户id
     * @return 权限列表
     */
    @Select("SELECT code FROM ilearn_menu WHERE id IN (SELECT menu_id FROM ilearn_permission WHERE role_id IN (SELECT role_id FROM ilearn_user_role WHERE user_id = #{userID}))")
    List<IlearnMenu> getUserMenus(@Param("userID") String userID);
}
