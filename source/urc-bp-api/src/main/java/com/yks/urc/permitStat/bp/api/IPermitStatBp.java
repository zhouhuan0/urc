package com.yks.urc.permitStat.bp.api;

import java.util.List;

import com.yks.urc.entity.UserPermissionCacheDO;

/**
 * 更新冗余表：urc_user_permission_cache/urc_user_permit_stat
 * 
 * @author panyun@youkeshu.com
 * @date 2018年6月13日 下午2:46:24
 * 
 */
public interface IPermitStatBp {
	/**
	 * 更新用户功能权限冗余表 and cache
	 * 
	 * @author panyun@youkeshu.com
	 * @date 2018年6月13日 下午2:47:34
	 */
	void updateUserPermitCache(List<String> lstUserName);

	/**
	 * 更新用户功能权限冗余表,并返回
	 * 
	 * @param userName
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月14日 下午1:06:15
	 */
	List<UserPermissionCacheDO> updateUserPermitCache(String userName);

	/**
	 * 更新用户功能权限冗余表,并返回sys对应的func
	 * 
	 * @param userName
	 * @param sysKey
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月14日 下午4:40:59
	 */
	UserPermissionCacheDO updateUserPermitCache(String userName, String sysKey);
}
