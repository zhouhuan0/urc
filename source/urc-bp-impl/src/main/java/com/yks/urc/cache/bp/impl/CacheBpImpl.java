package com.yks.urc.cache.bp.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.ignite.binary.Binarylizable;
import org.springframework.stereotype.Component;

import com.yks.distributed.cache.core.Cache;
import com.yks.distributed.cache.core.DistributedCache;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.vo.BizSysVO;
import com.yks.urc.vo.UserVO;

@Component
public class CacheBpImpl implements ICacheBp {

	/**
	 * 用户基础信息
	 * 
	 * @author panyun@youkeshu.com
	 * @date 2018年6月5日 上午10:07:11
	 */
	private Cache<String, UserVO> userInfoCache = new DistributedCache<>("URC-User", 2, TimeUnit.HOURS);

	/**
	 * 用户有功能权限的系统key/name
	 * 
	 * @author panyun@youkeshu.com
	 * @date 2018年6月5日 上午10:06:51
	 */
	private Cache<String, List<BizSysVO>> userSysKeyCache = new DistributedCache<>("URC-User-SysKeys", 1, TimeUnit.DAYS);

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
		userInfoCache.put(u.userName, u);
	}

	public UserVO getUser(String userName) {
		return userInfoCache.get(userName);
	}

	public void insertUserSysKey(String userName, List<BizSysVO> lst) {
		userSysKeyCache.put(userName, lst);
	}

	public List<BizSysVO> getUserSysKey(String userName) {
		return userSysKeyCache.get(userName);
	}

	public void insertUserSysFuncVersion(String userName, String sysKey, String funcVersion) {
		userSysFuncVersionCache.put(String.format("%s_%s", userName, sysKey), funcVersion);
	}

	public String getUserSysFuncVersion(String userName, String sysKey) {
		return userSysFuncVersionCache.get(String.format("%s_%s", userName, sysKey));
	}

	public void insertUserSysFuncJson(String userName, String sysKey, String funcJson) {
		userSysFuncJsonCache.put(String.format("%s_%s", userName, sysKey), funcJson);
	}

	public String getUserSysFuncJson(String userName, String sysKey) {
		return userSysFuncJsonCache.get(String.format("%s_%s", userName, sysKey));
	}

	public static void main(String[] args) {
		Cache<String, UserVO> cacheTest1 = new DistributedCache<String, UserVO>("cache-test-1");

		UserVO user = new UserVO();

		cacheTest1.put("player", user);

		// System.out.println("-----------------------" +
		// cacheTest1.get("player").getAge());
	}
}
