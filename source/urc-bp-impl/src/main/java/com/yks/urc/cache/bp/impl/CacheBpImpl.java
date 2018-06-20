package com.yks.urc.cache.bp.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.ignite.binary.Binarylizable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yks.distributed.cache.core.Cache;
import com.yks.distributed.cache.core.DistributedCache;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.entity.UserPermissionCacheDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.log.Log;
import com.yks.urc.log.LogLevel;
import com.yks.urc.user.bp.impl.UserBpImpl;
import com.yks.urc.vo.BizSysVO;
import com.yks.urc.vo.UserVO;

@Component
public class CacheBpImpl implements ICacheBp {
	private static Logger logger = LoggerFactory.getLogger(UserBpImpl.class);

	/**
	 * 用户基础信息
	 * 
	 * @author panyun@youkeshu.com
	 * @date 2018年6月5日 上午10:07:11
	 */
	private Cache<String, UserVO> userInfoCache = new DistributedCache<>("URC-User", 2, TimeUnit.HOURS);

	/**
	 * 用户有功能权限的系统key
	 * 
	 * @author panyun@youkeshu.com
	 * @date 2018年6月5日 上午10:06:51
	 */
	private Cache<String, List<String>> userSysKeyCache = new DistributedCache<>("URC-User-SysKeys");// , 1, TimeUnit.DAYS);

	/**
	 * 系统功能权限定义
	 * 
	 * @author panyun@youkeshu.com
	 * @date 2018年6月14日 下午3:00:25
	 */
	private Cache<String, String> sysFuncJsonCache = new DistributedCache<>("URC-SysFuncJson-Define");// , 100, TimeUnit.DAYS);

	/**
	 * 用户所有系统功能权限及版本号
	 * 
	 * @author panyun@youkeshu.com
	 * @date 2018年6月5日 上午10:06:36
	 */
	private Cache<String, List<UserPermissionCacheDO>> userFuncCache = new DistributedCache<>("URC-User-Sys-FuncVersion");// , 2, TimeUnit.HOURS);

	@Log(value = "insertUser", level = LogLevel.ERROR)
	public void insertUser(UserVO u) {
		try {
			userInfoCache.put(u.userName, u);
		} catch (Exception ex) {
			logger.error(String.format("insertUser:%s", StringUtility.toJSONString_NoException(u)), ex);
		}
	}

	public UserVO getUser(String userName) {
		try {
			return userInfoCache.get(userName);
		} catch (Exception ex) {
			logger.error(String.format("getUser:%s", userName), ex);
			return null;
		}
	}

	public void insertUserSysKey(String userName, List<String> lst) {
		try {
			userSysKeyCache.put(userName, lst);
		} catch (Exception ex) {
			logger.error(String.format("insertUserSysKey:%s %s", userName, StringUtility.toJSONString(lst)), ex);
		}
	}

	public List<String> getUserSysKey(String userName) {
		try {
			return userSysKeyCache.get(userName);
		} catch (Exception ex) {
			logger.error(String.format("getUserSysKey:%s", userName), ex);
			return null;
		}
	}

	public void insertUserFunc(String userName, List<UserPermissionCacheDO> lstPermitCache) {
		try {
			userFuncCache.put(userName, lstPermitCache);
		} catch (Exception ex) {
			logger.error(String.format("insertUserFunc:%s %s", userName, StringUtility.toJSONString_NoException(lstPermitCache)), ex);
		}
	}

	public List<UserPermissionCacheDO> getUserFunc(String userName) {
		try {
			return userFuncCache.get(userName);
		} catch (Exception ex) {
			logger.error(String.format("getUserFunc:%s", userName), ex);
			return null;
		}
	}

	public static void main(String[] args) {
		Cache<String, UserVO> cacheTest1 = new DistributedCache<String, UserVO>("cache-test-1");

		UserVO user = new UserVO();

		cacheTest1.put("player", user);

		// System.out.println("-----------------------" +
		// cacheTest1.get("player").getAge());
	}

	@Override
	public void removeUserSysKey(String userName) {
		try {
			userSysKeyCache.remove(userName);
		} catch (Exception ex) {
			logger.error(String.format("removeUserSysKey:%s", userName), ex);
		}
	}

	@Override
	public void removeUserFunc(String userName) {
		try {
			userFuncCache.remove(userName);
		} catch (Exception ex) {
			logger.error(String.format("removeUserFunc:%s", userName), ex);
		}
	}

	@Override
	public String getFuncVersion(String userName, String sysKey) {
		List<UserPermissionCacheDO> lstCache = this.getUserFunc(userName);
		if (lstCache != null) {
			for (UserPermissionCacheDO mem : lstCache) {
				if (StringUtility.stringEqualsIgnoreCase(mem.getSysKey(), sysKey)) {
					return mem.getPermissionVersion();
				}
			}
			return StringUtility.Empty;
		}
		return null;
	}

	@Override
	public String getSysContext(String sysKey) {
		try {
			return sysFuncJsonCache.get(sysKey);
		} catch (Exception ex) {
			logger.error(String.format("getSysContext:%s", sysKey), ex);
			return StringUtility.Empty;
		}
	}

	@Override
	public String getFuncJson(String operator, String sysKey) {
		List<UserPermissionCacheDO> lstCache = this.getUserFunc(operator);
		if (lstCache != null) {
			for (UserPermissionCacheDO mem : lstCache) {
				if (StringUtility.stringEqualsIgnoreCase(mem.getSysKey(), sysKey)) {
					return mem.getUserContext();
				}
			}
		}
		return StringUtility.Empty;
	}

	@Override
	public void insertSysContext(String sysKey, String sysContext) {
		sysFuncJsonCache.put(sysKey, sysContext);

	}

	@Override
	public void removeUser(String userName) {
		try {
			if (StringUtility.isNullOrEmpty(userName))
				return;
			userInfoCache.remove(userName);
		} catch (Exception ex) {
			logger.error(String.format("removeUser:%s", userName), ex);
		}
	}
}
