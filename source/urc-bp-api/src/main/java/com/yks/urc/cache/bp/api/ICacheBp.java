package com.yks.urc.cache.bp.api;

import java.util.List;

import com.yks.urc.entity.UserPermissionCacheDO;
import com.yks.urc.vo.GetAllFuncPermitRespVO;
import com.yks.urc.vo.UserVO;

public interface ICacheBp {
	UserVO getUser(String userName);

	/**
	 * 获取用户所有系统的功能权限
	 * @param userName
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月21日 下午3:30:56
	 */
	GetAllFuncPermitRespVO getUserFunc(String userName);

//	List<String> getUserSysKey(String userName);

	void insertUser(UserVO u);
	void removeUser(String userName);

	/**
	 * 缓存用户所有系统功能权限
	 * @param userName
	 * @param permitCache
	 * @author panyun@youkeshu.com
	 * @date 2018年6月21日 下午8:01:38
	 */
	void insertUserFunc(String userName, GetAllFuncPermitRespVO permitCache);


//	void insertUserSysKey(String userName, List<String> lst);

//	void removeUserSysKey(String userName);

	/**
	 * 获取某个user的功能权限版本
	 * 
	 * @param userName
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月14日 下午2:23:29
	 */
	String getFuncVersion(String userName);

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
	String getFuncJson(String userName, String sysKey);

	void insertSysContext(String sysKey, String sysContext);
}
