package com.yks.urc.cache.bp.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.cache.bp.api.ILocalCacheBp;
import com.yks.urc.lock.bp.impl.RedissonLockBpImpl;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存，redis更新了，会自动更新本地缓存，比 INewCacheBp 快N倍,
 * 适用于高频率访问redis的场景，例如：导出时，包裹状态编码转文字，国家编码转文字
 *
 * @return
 * @Author panyun@youkeshu.com
 * @Date 2020-05-22 12:13
 */
@Component
public class LocalCacheBpImpl implements ILocalCacheBp {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedissonLockBpImpl redissonLockBp;

    // https://yq.aliyun.com/articles/551645
    LocalCachedMapOptions options = LocalCachedMapOptions.defaults();

    private Map<String, RLocalCachedMap<String, String>> cacheMap = new HashMap<String, RLocalCachedMap<String, String>>();

    private RLocalCachedMap<String, String> getCache(String name) {
        if (cacheMap.containsKey(name)) {
            return cacheMap.get(name);
        }
        RLocalCachedMap<String, String> map = redissonLockBp.getRedisson().getLocalCachedMap(name, options);
        cacheMap.put(name, map);
        return map;
    }

    @Autowired
    private ISerializeBp serializeBp;

    private String getCacheName(String key) {
        return String.format("lc:%s", key);
    }

    public <T> void setLocalCache(String cacheName, T value) {
        setLocalCache(cacheName, "v", value);
    }

    @Override
    public <T> void setLocalCache(String cacheName, T value, long expireMs) {
        setLocalCache(cacheName, "v", value, expireMs);
    }

    private <T> void setLocalCache(String cacheName, String key, T value, long expireMs) {
        if (value == null) {
            return;
        }
        RLocalCachedMap<String, String> map = getCache(getCacheName(cacheName));
        if (expireMs > -1) {
            map.expire(expireMs, TimeUnit.MILLISECONDS);
        }
        map.put(key, serializeBp.obj2JsonNonEmpty(value));
    }

    @Override
    public <T> void setLocalCache(String cacheName, String key, T value) {
        setLocalCache(cacheName, key, value, -1);
    }

    TypeReference<String> tString = new TypeReference<String>() {
    };

    public String getLocalCache(String cacheName) {
        return getLocalCache(cacheName, "v");
    }

    @Override
    public <T> T getLocalCache(String cacheName, TypeReference<T> t) {
        return getLocalCache(cacheName, "v", t);
    }

    @Override
    public String getLocalCache(String cacheName, String key) {
        return getLocalCache(cacheName, key, tString);
    }

    @Override
    public <T> T getLocalCache(String cacheName, String key, TypeReference<T> t) {
        RLocalCachedMap<String, String> map = getCache(getCacheName(cacheName));
        String rslt = map.get(key);
        return serializeBp.json2ObjNew(rslt, t);
    }
}
