package com.ilearn.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ilearn.system.model.po.Dictionary;

import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author xiaoxiaoyi
 * @since 2023-01-26
 */
public interface DictionaryService extends IService<Dictionary> {

    /**
     * 查询所有数据字典内容
     *
     * @return 数据字典所有内容
     */
    List<Dictionary> queryAll();

    /**
     * 根据code查询数据字典
     *
     * @param code -- String 数据字典Code
     * @return code对应的数据字典内容
     */
    Dictionary getByCode(String code);
}
