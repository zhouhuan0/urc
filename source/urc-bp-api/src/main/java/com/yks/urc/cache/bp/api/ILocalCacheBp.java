package com.yks.urc.cache.bp.api;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 使用redission实现，本地缓存
 *
 * @return
 * @Author panyun@youkeshu.com
 * @Date 2019-12-13 08:50
 */
public interface ILocalCacheBp {
    <T> void setLocalCache(String cacheName, T value);

    <T> void setLocalCache(String cacheName, String key, T value);

    <T> void setLocalCache(String cacheName, T value, long expireMs);

    String getLocalCache(String cacheName);

    <T> T getLocalCache(String cacheName, TypeReference<T> t);


    String getLocalCache(String cacheName, String key);

    <T> T getLocalCache(String cacheName, String key, TypeReference<T> t);
}
