/**
 * @(#)JsonUtil.java 2014-2-23 下午5:44:19
 */
package com.ilearn.base.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JsonUtil {

    public static @NotNull String objectToJson(Object object) {
        return JSON.toJSONString(object, SerializerFeature.WriteDateUseDateFormat);
    }


    public static @NotNull String listToJson(List<?> list) {
        return JSON.toJSONString(list, SerializerFeature.WriteDateUseDateFormat);
    }

    /**
     * 字符串Json格式转换为对象Map
     *
     * @param strJson {"username":"sxb"}
     * @return 根据json转换为Map对象
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jsonToMap(String strJson) {
        Map<String, Object> jsoMap = new HashMap<>();
        try {
            jsoMap = JSONObject.parseObject(strJson, Map.class);
        } catch (JSONException e) {
            System.out.println("json转换Map出错：" + e.getMessage());
        }

        return jsoMap;
    }

    public static <T> @Nullable T jsonToObject(String strJson, Class<T> tClass) {
        try {
            return JSON.parseObject(strJson, tClass);
        } catch (JSONException e) {
            System.out.println("json转换Map出错：" + e.getMessage());
        }
        return null;
    }


    /**
     * 字符串Json 转换为对象List
     *
     * @param strJson [{"username":"sxb"}]
     * @return 根据json转换List
     */
    public static <T> @Nullable List<T> jsonToList(String strJson, Class<T> tClass) {
        try {
            return JSONObject.parseArray(strJson, tClass);
        } catch (JSONException e) {
            System.out.println("json转换List出错：" + e.getMessage());
        }
        return null;
    }


}
