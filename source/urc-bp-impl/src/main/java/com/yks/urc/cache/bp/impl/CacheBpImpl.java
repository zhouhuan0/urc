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
import com.yks.urc.fw.StringUtility;
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
	private Cache<String, List<String>> userSysKeyCache = new DistributedCache<>("URC-User-SysKeys", 1, TimeUnit.DAYS);

	/**
	 * 用户某个系统功能权限版本号
	 * 
	 * @author panyun@youkeshu.com
	 * @date 2018年6月5日 上午10:06:36
	 */
	private Cache<String, String> userSysFuncVersionCache = new DistributedCache<>("URC-User-Sys-FuncVersion", 2, TimeUnit.HOURS);
	/**
	 * 用户某个系统系统功能权限json
	 * 
	 * @author panyun@youkeshu.com
	 * @date 2018年6月5日 上午10:06:20
	 */
	private Cache<String, String> userSysFuncJsonCache = new DistributedCache<>("URC-User-Sys-FuncJson", 2, TimeUnit.HOURS);

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

	public void insertUserSysFuncVersion(String userName, String sysKey, String funcVersion) {
		try {
			userSysFuncVersionCache.put(String.format("%s_%s", userName, sysKey), funcVersion);
		} catch (Exception ex) {
			logger.error(String.format("insertUserSysFuncVersion:%s %s %s", userName, sysKey, funcVersion), ex);
		}
	}

	public String getUserSysFuncVersion(String userName, String sysKey) {
		try {
			return userSysFuncVersionCache.get(String.format("%s_%s", userName, sysKey));
		} catch (Exception ex) {
			logger.error(String.format("getUserSysFuncVersion:%s %s", userName, sysKey), ex);
			return null;
		}
	}

	public void insertUserSysFuncJson(String userName, String sysKey, String funcJson) {
		try {
			userSysFuncJsonCache.put(String.format("%s_%s", userName, sysKey), funcJson);
		} catch (Exception ex) {
			logger.error(String.format("insertUserSysFuncJson:%s %s %s", userName, sysKey, funcJson), ex);
		}
	}

	public String getUserSysFuncJson(String userName, String sysKey) {
		try {
			return userSysFuncJsonCache.get(String.format("%s_%s", userName, sysKey));
		} catch (Exception ex) {
			logger.error(String.format("getUserSysFuncJson:%s %s", userName, sysKey), ex);
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
}
