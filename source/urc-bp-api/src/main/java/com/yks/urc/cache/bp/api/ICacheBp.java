package com.yks.urc.cache.bp.api;

import java.util.List;

import com.yks.urc.entity.UserPermissionCacheDO;
import com.yks.urc.vo.UserVO;

public interface ICacheBp {
	UserVO getUser(String userName);

	List<UserPermissionCacheDO> getUserFunc(String userName);

	List<String> getUserSysKey(String userName);

	void insertUser(UserVO u);
	void removeUser(String userName);

	void insertUserFunc(String userName, List<UserPermissionCacheDO> lstPermitCache);

	void removeUserFunc(String userName);

	void insertUserSysKey(String userName, List<String> lst);

	void removeUserSysKey(String userName);

	/**
	 * 获取某个sysKey的功能权限版本
	 * 
	 * @param userName
	 * @param sysKey
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月14日 下午2:23:29
	 */
	String getFuncVersion(String userName, String sysKey);

	/**
	 * 获取sys功能权限json,null表示缓存中没有，需要查db;Empty则不需要查db
	 * 
	 * @param sysKey
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月14日 下午2:46:02
	 */
	String getSysContext(String sysKey);

	/**
	 * 获取用户某个sys功能权限json
	 * 
	 * @param operator
	 * @param sysKey
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月14日 下午2:46:21
	 */
	String getFuncJson(String operator, String sysKey);

	void insertSysContext(String sysKey, String sysContext);
}
