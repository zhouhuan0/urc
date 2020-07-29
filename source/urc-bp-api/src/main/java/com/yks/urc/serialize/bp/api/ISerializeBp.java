package com.yks.urc.serialize.bp.api;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * json/xml序列反序列化
 *
 * @return
 * @Author panyun@youkeshu.com
 * @Date 2018/10/29 16:30
 */
public interface ISerializeBp {
    String obj2Json(Object obj);
    /**
     * 过滤为null和“”的属性
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2019-11-09 17:18
     */
    String obj2JsonNonEmpty(Object obj);

    /**
     * 此方法支持泛型
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2019/5/3 16:04
     */
    <T> T json2ObjNew(String strJson, TypeReference<T> t);

    /**
     * 此方法不支持泛型
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2019/5/3 16:04
     */
    <T> T json2Obj(String strJson, Class<T> valueType);
}
