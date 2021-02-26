package com.yks.urc.cache.bp.impl;

import com.yks.distributed.cache.core.Cache;
import com.yks.distributed.cache.core.DistributedCacheBuilder;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.constant.UrcConstant;
import com.yks.urc.entity.PermissionDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.log.Log;
import com.yks.urc.log.LogLevel;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.vo.GetAllFuncPermitRespVO;
import com.yks.urc.vo.UserSysVO;
import com.yks.urc.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
public class RedisCacheBpImpl2 implements ICacheBp {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    Map<String, Cache> mapCache = new HashMap<>();

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 获取用户基础信息缓存,2天
     *
     * @param userName
     * @return
     */
    private Cache getUserInfoCache(String userName) {
        return getCache(String.format("u_info_%s", userName), 172800);
    }

    private Cache getCache(String cacheName, int expire) {
        if (!mapCache.containsKey(cacheName)) {
            DistributedCacheBuilder b = DistributedCacheBuilder.newBuilder().config("/cache.properties");
            b.cacheName(cacheName);
            if (expire >= -1) {
                b.expire(expire);
            }
            mapCache.put(cacheName, b.build());
        }
        return mapCache.get(cacheName);
    }

    private Cache getCache(String cacheName) {
        return getCache(cacheName, -1);

    }

    @Log(value = "insertUser", level = LogLevel.INFO)
    public void insertUser(UserVO u) {
        if (u == null || StringUtility.isNullOrEmpty(u.ip) || StringUtility.isNullOrEmpty(u.ticket) ||
                StringUtility.isNullOrEmpty(u.userName)) return;
        try {
            Map<String, String> mapUser = new HashMap<>();
            if (!StringUtility.isNullOrEmpty(u.ip)) {
                mapUser.put(StringConstant.ip, u.ip);
            }
            if (!StringUtility.isNullOrEmpty(u.ticket)) {
                mapUser.put(StringConstant.ticket, u.ticket);
            }
            if (!StringUtility.isNullOrEmpty(u.userName)) {
                mapUser.put(StringConstant.userName, u.userName);
            }
            if (!StringUtility.isNullOrEmpty(u.deviceName)) {
                mapUser.put(StringConstant.deviceName, u.deviceName);
            }
            if (!StringUtility.isNullOrEmpty(u.deviceType)) {
                mapUser.put(StringConstant.deviceType, u.deviceType);
            }
            if (u.loginTime != null) {
                mapUser.put(StringConstant.loginTime, u.loginTime.toString());
            }
            getUserLoginCache(u.userName,u.deviceType).put(u.userName, StringUtility.toJSONString_NoException(mapUser));
        } catch (Exception ex) {
            logger.error(String.format("insertUser:%s", StringUtility.toJSONString_NoException(u)), ex);
        }
    }

    private final static String WhiteApiKey = "white_api";

    @Override
    public void insertWhiteApi(String apiStr) {
        try {
            getCache(WhiteApiKey).put("api", apiStr);
        } catch (Exception e) {
            logger.error(String.format("Cache whitelisting failed:%s", apiStr), e);
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

    private String getCacheKey_UserLogin(String userName, String deviceType) {
        // 不同设备使用不同的缓存，达到不同设备各维持一个设备在线
        if (StringUtility.isNullOrEmpty(deviceType)) {
            // deviceType为空时，默认为PC端
            return String.format("urc_user_login_%s", userName);
        }
        return String.format("urc_user_login_%s_%s", userName, deviceType);
    }

//    private Cache getUserLoginCache(String userName) {
//        return getCache(getCacheKey_UserLogin(userName,null), 7200);
//    }

    private Cache getUserLoginCache(String userName,String deviceType) {
        return getCache(getCacheKey_UserLogin(userName,deviceType), 7200);
    }

//    public UserVO getUser(String userName) {
//        return getUser(userName,null);
//    }

    @Override
    public UserVO getUser(String userName, String deviceType){
        try {
            String strUser = (String) getUserLoginCache(userName,deviceType).get(userName);
            return StringUtility.parseObject(strUser, UserVO.class);
            // return userInfoCache.get(userName);
        } catch (Exception ex) {
            logger.error(String.format("getUser:%s", userName), ex);
            return null;
        }
    }

    public String getWhiteApi(String str) {
        String api = null;
        try {
            Object object = getCache(WhiteApiKey).get("api");
            if (object != null) {
                api = object.toString();
            }
        } catch (Exception e) {
            logger.error(String.format("getWhiteApi:%s", str), e);
        }
        return api;
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
            // 缓存用户的所有系统功能权限
            getCache(getCacheKey_UserSysFunc(userName)).clear();
            getCache(getCacheKey_UserSysFunc(userName)).batchPut(map);
            // 缓存用户功能权限版本号
            getCache(getCacheKey_UserFuncVersion()).put(userName, StringUtility.isNullOrEmpty(permitCache.funcVersion) ? NA : permitCache.funcVersion);
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

    public GetAllFuncPermitRespVO getUserFunc(String userName,List<String> sysKeys,Integer sysType) {
        Map<String, String> mapHash = getCache(getCacheKey_UserSysFunc(userName)).getAll();
        if (mapHash != null && mapHash.size() > 0) {
            // 按sysKeyr排序，前端顶部导航栏依赖此顺序
            TreeMap<String, String> map = new TreeMap<>();
            map.putAll(mapHash);
            GetAllFuncPermitRespVO rslt = new GetAllFuncPermitRespVO();
            rslt.lstSysRoot = new ArrayList<>();
            Iterator<String> it = map.keySet().iterator();

            while (it.hasNext()) {
                String key = it.next();
                if (StringUtility.stringEqualsIgnoreCase("NA", key))
                    continue;

                PermissionDO permissionBySysKey = permissionMapper.getPermissionBySysKey(key);
                if(permissionBySysKey == null){
                    continue;
                }

                //过滤禁用状态的系统
                if(permissionBySysKey.getStatus() != null && StringUtility.stringEqualsIgnoreCaseObj(permissionBySysKey.getStatus(),0)){
                    continue;
                }

                if(sysType != null){
                    //排除系统
                    if(!StringUtility.stringEqualsIgnoreCaseObj(permissionBySysKey.getSysType(), sysType) ){
                        continue;
                    }
                }

                if(!CollectionUtils.isEmpty(sysKeys)){
                	for (String sysKey : sysKeys) {
						if(key.equals(sysKey)){
							rslt.lstSysRoot.add(map.get(key));
							break;
						}
					}
                }else{
                	rslt.lstSysRoot.add(map.get(key));
                }
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

    @Override
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
    public void removeUser(String userName,String deviceType) {
        try {
            if (StringUtility.isNullOrEmpty(userName))
                return;
            getUserLoginCache(userName, deviceType).clear();
        } catch (Exception ex) {
            logger.error(String.format("removeUser:%s %s", userName, deviceType), ex);
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

    private String KEY_person = "person";

    @Override
    public String getPersonByUserName(String userName) {
        return StringUtility.addEmptyString(getUserInfoCache(userName).get(KEY_person));
    }

    @Override
    public void setPersonByUserName(String userName, String person) {
        getUserInfoCache(userName).put(KEY_person, person);
    }

    private String Key_platform_shop ="ebay_platform_shop";
    private String Key_all_platform_shop ="all_platform_shop";
    @Override
    public String getAllPlatformShop(String platformShopKey,String entityCode) {
        if (StringUtility.stringEqualsIgnoreCase("E_PlsShopAccount",entityCode)) {
            return StringUtility.addEmptyString(getPlatformShopCache(Key_platform_shop).get(Key_platform_shop));
        }else {
            return StringUtility.addEmptyString(getPlatformShopCache(Key_all_platform_shop).get(Key_all_platform_shop));
        }
    }

    @Override
    public void setAllPlatformShop(String allPlatformShopJson,String entityCode) {
        if (StringUtility.stringEqualsIgnoreCase("E_PlsShopAccount",entityCode)) {
            getPlatformShopCache(Key_platform_shop).put(Key_platform_shop, allPlatformShopJson);
        }else {
            getPlatformShopCache(Key_all_platform_shop).put(Key_all_platform_shop, allPlatformShopJson);
        }
    }

    @Override
    public void refreshUserExpiredTime(String userName,String deviceType) {
        insertUser(getUser(userName,deviceType));
    }

    /**
    *  获取所有的平台账号 缓存2H
    * @param
    * @return
    * @Author lwx
    * @Date 2018/9/4 16:54
    */
    private Cache getPlatformShopCache(String platformShopKey) {
        return getCache(platformShopKey,7200);
    }
}
