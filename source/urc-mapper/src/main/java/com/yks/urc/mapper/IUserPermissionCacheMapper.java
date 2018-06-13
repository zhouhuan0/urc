package com.yks.urc.mapper;

import com.yks.urc.entity.UserPermissionCache;

public interface IUserPermissionCacheMapper {

	/**
	 * delete urc_user_permission_cache table
	 * 
	 * @param userName
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月13日 下午3:00:40
	 */
	int deletePermitCacheByUser(String userName);

	/**
	 * delete urc_user_permit_stat table
	 * 
	 * @param userName
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月13日 下午3:00:35
	 */
	int deletePermitStatByUser(String userName);

}