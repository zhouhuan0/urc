package com.yks.urc.cache.bp.impl;

import com.yks.distributed.cache.core.Cache;
import com.yks.distributed.cache.core.DistributedCache;
import com.yks.distributed.cache.core.DistributedCacheBuilder;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.entity.PermissionDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.log.Log;
import com.yks.urc.log.LogLevel;
import com.yks.urc.user.bp.impl.UserBpImpl;
import com.yks.urc.vo.GetAllFuncPermitRespVO;
import com.yks.urc.vo.UserSysVO;
import com.yks.urc.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.*;

@Component
public class RedisCacheBpImpl2 implements ICacheBp {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    Map<String, Cache> mapCache = new HashMap<>();

    private Cache getCache(String cacheName, int expire) {
        if (!mapCache.containsKey(cacheName)) {
            DistributedCacheBuilder b = DistributedCacheBuilder.newBuilder().config("/cache.properties");
            b.cacheName(cacheName);
            if (expire > 0) {
                b.expire(expire);
            }
            mapCache.put(cacheName, b.build());
        }
        return mapCache.get(cacheName);
    }

    private Cache getCache(String cacheName) {
        return getCache(cacheName, -1);

    }

    @Log(value = "insertUser", level = LogLevel.ERROR)
    public void insertUser(UserVO u) {
        if (u == null || StringUtility.isNullOrEmpty(u.ip) || StringUtility.isNullOrEmpty(u.ticket) ||
                StringUtility.isNullOrEmpty(u.userName)) return;
        try {
            Map<String, String> mapUser = new HashMap<>();
            mapUser.put(StringConstant.ip, u.ip);
            mapUser.put(StringConstant.ticket, u.ticket);
            mapUser.put(StringConstant.userName, u.userName);
            getUserLoginCache(u.userName).put(u.userName, StringUtility.toJSONString_NoException(mapUser));
        } catch (Exception ex) {
            logger.error(String.format("insertUser:%s", StringUtility.toJSONString_NoException(u)), ex);
        }
    }

    public long getNextSeq(String strKey) {
        try {
            return (long) getCache("urc_seq").incrSequence(strKey);
        } catch (Exception ex) {
            logger.error(String.format("getNextSeq:%s", strKey), ex);
        }
        return (long) (Math.random() * 100000);
    }

    private String getCacheKey_UserLogin(String userName) {
        return String.format("urc_user_login_%s", userName);
    }

    private Cache getUserLoginCache(String userName) {
        return getCache(getCacheKey_UserLogin(userName), 7200);
    }

    public UserVO getUser(String userName) {
        try {
            String strUser = (String) getUserLoginCache(userName).get(userName);
            return StringUtility.parseObject(strUser, UserVO.class);
            // return userInfoCache.get(userName);
        } catch (Exception ex) {
            logger.error(String.format("getUser:%s", userName), ex);
            return null;
        }
    }

    private static final String NA = "NA";

    public void insertUserFunc(String userName, GetAllFuncPermitRespVO permitCache) {
        try {
            Map<String, String> map = new HashMap<>();
            for (UserSysVO u : permitCache.lstUserSysVO) {
                map.put(u.sysKey, u.context);
            }
            if (map.size() == 0) {
                map.put(NA, NA);
            }
            if (StringUtility.isNullOrEmpty(permitCache.funcVersion))
                permitCache.funcVersion = NA;
            // 缓存用户的所有系统功能权限
            getCache(getCacheKey_UserSysFunc(userName)).batchPut(map);
            // 缓存用户功能权限版本号
            getCache(getCacheKey_UserFuncVersion()).put(userName, permitCache.funcVersion);
        } catch (Exception ex) {
            logger.error(String.format("insertUserFunc:%s %s", userName, StringUtility.toJSONString_NoException(permitCache)), ex);
        }
    }

    private String getCacheKey_UserFuncVersion() {
        return "user_func_version";
    }

    /**
     * 获取用户功能权限缓存key
     *
     * @param userName
     * @return
     * @author panyun@youkeshu.com
     * @date 2018年6月21日 下午8:34:48
     */
    private String getCacheKey_UserSysFunc(String userName) {
        return String.format("user_sys_func_%s", userName);
    }

    public GetAllFuncPermitRespVO getUserFunc(String userName) {
        Map<String, String> map = getCache(getCacheKey_UserSysFunc(userName)).getAll();
        if (map != null && map.size() > 0) {
            GetAllFuncPermitRespVO rslt = new GetAllFuncPermitRespVO();
            rslt.lstSysRoot = new ArrayList<>();
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (StringUtility.stringEqualsIgnoreCase("NA", key))
                    continue;
                rslt.lstSysRoot.add(map.get(key));
            }
            rslt.funcVersion = getFuncVersion(userName);
            return rslt;
        }
        return null;
    }

    public List<PermissionDO> getSysApiUrlPrefix() {
        try {
            Map<String, String> map = getCache(getCacheKey_SysApiUrlPrefix()).getAll();
            if (map == null || map.size() == 0)
                return null;
            List<PermissionDO> lstRslt = new ArrayList<>();
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String strKey = it.next();
                if (StringUtility.stringEqualsIgnoreCase(NA, strKey))
                    continue;
                PermissionDO p = new PermissionDO();
                p.setSysKey(strKey);
                p.setApiUrlPrefixJson(map.get(strKey));
                lstRslt.add(p);
            }
            return lstRslt;
        } catch (Exception ex) {
            logger.error("getSysApiUrlPrefix", ex);
        }
        return null;
    }

    public void setSysApiUrlPrefix(List<PermissionDO> lst) {
        try {
            String strKey = getCacheKey_SysApiUrlPrefix();
            Map<String, String> map = new HashMap<>();
            if (lst == null || lst.size() == 0) {
                map.put(NA, NA);
            } else {
                for (PermissionDO p : lst) {
                    map.put(p.getSysKey(), p.getApiUrlPrefixJson());
                }
            }
            getCache(strKey).clear();
            getCache(strKey).batchPut(map);
        } catch (Exception ex) {
            logger.error(String.format("setSysApiUrlPrefix:%s", StringUtility.toJSONString_NoException(lst)), ex);
        }
    }

    private String getCacheKey_SysApiUrlPrefix() {
        return "sys_api_url_prefix";
    }

    @Override
    public String getFuncVersion(String userName) {
        Object rslt = getCache(getCacheKey_UserFuncVersion()).get(userName);
        if (rslt == null) return null;
        String strRslt = StringUtility.addEmptyString(rslt);
        if (StringUtility.stringEqualsIgnoreCase(NA, strRslt))
            return StringUtility.Empty;
        return strRslt;
    }

    private String getCacheKey_SysContext() {
        return "sys_context";
    }

    @Override
    public String getSysContext(String sysKey) {
        try {
            Object rslt = getCache(getCacheKey_SysContext()).get(sysKey);
            if (rslt == null) return null;

            String strRslt = StringUtility.addEmptyString(rslt);
            if (StringUtility.stringEqualsIgnoreCase(strRslt, NA)) return StringUtility.Empty;
            return strRslt;
        } catch (Exception ex) {
            logger.error(String.format("getSysContext:%s", sysKey), ex);
            return StringUtility.Empty;
        }
    }

    @Override
    public String getFuncJson(String userName, String sysKey) {
        return StringUtility.addEmptyString(getCache(getCacheKey_UserSysFunc(userName)).get(sysKey));
    }

    @Override
    public void insertSysContext(String sysKey, String sysContext) {
        if (StringUtility.isNullOrEmpty(sysKey))
            return;
        if (StringUtility.isNullOrEmpty(sysContext)) sysContext = NA;
        getCache(getCacheKey_SysContext()).put(sysKey, sysContext);
    }

    @Override
    public void removeUser(String userName) {
        try {
            if (StringUtility.isNullOrEmpty(userName))
                return;
            getUserLoginCache(userName).clear();
        } catch (Exception ex) {
            logger.error(String.format("removeUser:%s", userName), ex);
        }
    }

    public String getDingAccessToken(String accessTokeTime) {
        try {
            String accessToke = StringUtility.addEmptyString(getCache(accessTokeTime).get(accessTokeTime));
            return accessToke;
        } catch (Exception ex) {
            logger.error(String.format("getDingAccessToken:%s", accessTokeTime), ex);
            return null;
        }
    }

    public void setDingAccessToken(String accessTokeTime, String accessTokeValue) {
        if (StringUtility.isNullOrEmpty(accessTokeTime))
            return;
        getCache(accessTokeTime).put(accessTokeTime, accessTokeValue);
    }
}
