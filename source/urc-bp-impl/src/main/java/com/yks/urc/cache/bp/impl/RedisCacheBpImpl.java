package com.yks.urc.cache.bp.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.ignite.binary.Binarylizable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yks.distributed.cache.core.Cache;
import com.yks.distributed.cache.core.DistributedCache;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.entity.Permission;
import com.yks.urc.entity.UserPermissionCacheDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.log.Log;
import com.yks.urc.log.LogLevel;
import com.yks.urc.user.bp.impl.UserBpImpl;
import com.yks.urc.vo.BizSysVO;
import com.yks.urc.vo.GetAllFuncPermitRespVO;
import com.yks.urc.vo.PermissionVO;
import com.yks.urc.vo.UserSysVO;
import com.yks.urc.vo.UserVO;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Component
public class RedisCacheBpImpl implements ICacheBp {
	private static Logger logger = LoggerFactory.getLogger(UserBpImpl.class);

	@Autowired
	private ShardedJedisPool shardedJedisPool;

	@Log(value = "insertUser", level = LogLevel.ERROR)
	public void insertUser(UserVO u) {
		try {
			setKey(getCacheKey_UserLogin(u.userName), StringUtility.toJSONString_NoException(u), 7200);
		} catch (Exception ex) {
			logger.error(String.format("insertUser:%s", StringUtility.toJSONString_NoException(u)), ex);
		}
	}

	private void setKey(String strKey, String strValue, int exSecond) {
		ShardedJedis shardJedis = null;
		try {
			shardJedis = shardedJedisPool.getResource();
			// 存入键值对
			if (exSecond == 0) {
				shardJedis.set(strKey, strValue);
			} else {
				shardJedis.setex(strKey, exSecond, strValue);
			}
		} catch (Exception ex) {
			logger.error(String.format("setKey:%s %s", strKey, strValue), ex);
		} finally {
			// 回收ShardedJedis实例
			if (shardJedis != null)
				shardJedis.close();
		}
	}

	private void delKey(String strKey) {
		ShardedJedis shardJedis = null;
		try {
			shardJedis = shardedJedisPool.getResource();
			shardJedis.del(strKey);
		} catch (Exception ex) {
			logger.error(String.format("delKey:%s %s", strKey), ex);
		} finally {
			// 回收ShardedJedis实例
			if (shardJedis != null)
				shardJedis.close();
		}
	}

	public long getNextSeq(String strKey) {
		ShardedJedis shardJedis = null;
		try {
			shardJedis = shardedJedisPool.getResource();
			return shardJedis.incr(strKey);
		} catch (Exception ex) {
			logger.error(String.format("getNextSeq:%s", strKey), ex);
		} finally {
			// 回收ShardedJedis实例
			if (shardJedis != null)
				shardJedis.close();
		}
		return (long) (Math.random() * 100000);
	}

	private String getKey(String strKey) {
		ShardedJedis shardJedis = null;
		try {
			shardJedis = shardedJedisPool.getResource();
			return shardJedis.get(strKey);

		} catch (Exception ex) {
			logger.error(String.format("getKey:%s %s", strKey), ex);
		} finally {
			// 回收ShardedJedis实例
			if (shardJedis != null)
				shardJedis.close();
		}
		return null;
	}

	private String getCacheKey_UserLogin(String userName) {
		return String.format("user_login_%s", userName);
	}

	public UserVO getUser(String userName) {
		try {
			String strUser = getKey(getCacheKey_UserLogin(userName));
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
			hmset(getCacheKey_UserSysFunc(userName), map);
			setKey(getCacheKey_UserFuncVersion(userName), permitCache.funcVersion, 0);
			// userFuncCache.put(userName, permitCache);
		} catch (Exception ex) {
			logger.error(String.format("insertUserFunc:%s %s", userName, StringUtility.toJSONString_NoException(permitCache)), ex);
		}
	}

	private String getCacheKey_UserFuncVersion(String userName) {
		return String.format("user_func_version_%s", userName);
	}

	private void hmset(String strKey, Map<String, String> map) {
		ShardedJedis shardJedis = null;
		try {
			shardJedis = shardedJedisPool.getResource();
			shardJedis.hmset(strKey, map);
		} catch (Exception ex) {
			logger.error(String.format("hmset:%s %s", strKey, StringUtility.toJSONString_NoException(map)), ex);
		} finally {
			// 回收ShardedJedis实例
			if (shardJedis != null)
				shardJedis.close();
		}
	}

	private Map<String, String> hgetAll(String strKey) {
		ShardedJedis shardJedis = null;
		try {
			shardJedis = shardedJedisPool.getResource();
			if (!shardJedis.exists(strKey))
				return null;
			Map<String, String> map = shardJedis.hgetAll(strKey);
			return map;
		} catch (Exception ex) {
			logger.error(String.format("hgetAll:%s", strKey), ex);
		} finally {
			// 回收ShardedJedis实例
			if (shardJedis != null)
				shardJedis.close();
		}
		return null;
	}

	private String hget(String strKey, String field) {
		ShardedJedis shardJedis = null;
		try {
			shardJedis = shardedJedisPool.getResource();
			return shardJedis.hget(strKey, field);
		} catch (Exception ex) {
			logger.error(String.format("hgetAll:%s", strKey), ex);
		} finally {
			// 回收ShardedJedis实例
			if (shardJedis != null)
				shardJedis.close();
		}
		return null;
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
		Map<String, String> map = hgetAll(getCacheKey_UserSysFunc(userName));
		if (map != null) {
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

	public List<Permission> getSysApiUrlPrefix() {
		try {
			Map<String, String> map = hgetAll(getCacheKey_SysApiUrlPrefix());
			if (map == null)
				return null;
			List<Permission> lstRslt = new ArrayList<>();
			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {
				String strKey = it.next();
				if (StringUtility.stringEqualsIgnoreCase(NA, strKey))
					continue;
				Permission p = new Permission();
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

	public void setSysApiUrlPrefix(List<Permission> lst) {
		try {
			String strKey = getCacheKey_SysApiUrlPrefix();
			Map<String, String> map = new HashMap<>();
			if (lst == null || lst.size() == 0) {
				map.put(NA, NA);
			} else {
				for (Permission p : lst) {
					map.put(p.getSysKey(), p.getApiUrlPrefixJson());
				}
			}
			hmset(strKey, map);
		} catch (Exception ex) {
			logger.error(String.format("setSysApiUrlPrefix:%s", StringUtility.toJSONString_NoException(lst)), ex);
		}
	}

	private String getCacheKey_SysApiUrlPrefix() {
		return "sys_api_url_prefix";
	}

	public static void main(String[] args) {
		Cache<String, UserVO> cacheTest1 = new DistributedCache<String, UserVO>("cache-test-1");

		UserVO user = new UserVO();

		cacheTest1.put("player", user);

		// System.out.println("-----------------------" +
		// cacheTest1.get("player").getAge());
	}

	@Override
	public String getFuncVersion(String userName) {
		String strRslt = getKey(getCacheKey_UserFuncVersion(userName));
		if (StringUtility.stringEqualsIgnoreCase(NA, strRslt))
			return StringUtility.Empty;
		return strRslt;
	}

	private String getCacheKey_SysContext(String sysKey) {
		return String.format("sys_context_%s", sysKey);
	}

	@Override
	public String getSysContext(String sysKey) {
		try {
			return getKey(getCacheKey_SysContext(sysKey));
		} catch (Exception ex) {
			logger.error(String.format("getSysContext:%s", sysKey), ex);
			return StringUtility.Empty;
		}
	}

	@Override
	public String getFuncJson(String userName, String sysKey) {
		return hget(getCacheKey_UserSysFunc(userName), sysKey);
	}

	@Override
	public void insertSysContext(String sysKey, String sysContext) {
		if (StringUtility.isNullOrEmpty(sysKey))
			return;
		setKey(getCacheKey_SysContext(sysKey), sysContext, 0);
		// sysFuncJsonCache.put(sysKey, sysContext);
	}

	@Override
	public void removeUser(String userName) {
		try {
			if (StringUtility.isNullOrEmpty(userName))
				return;
			delKey(userName);
		} catch (Exception ex) {
			logger.error(String.format("removeUser:%s", userName), ex);
		}
	}
	
	public String getDingAccessToken(String accessTokeTime) {
		try {
			String accessToke= getKey(accessTokeTime);
			return accessToke;
		} catch (Exception ex) {
			logger.error(String.format("getDingAccessToken:%s", accessTokeTime), ex);
			return null;
		}
	}
	
	
	public void setDingAccessToken(String accessTokeTime,String accessTokeValue) {
		if (StringUtility.isNullOrEmpty(accessTokeTime))
			return;
		setKey(accessTokeTime, accessTokeValue, 0);
	}
}
