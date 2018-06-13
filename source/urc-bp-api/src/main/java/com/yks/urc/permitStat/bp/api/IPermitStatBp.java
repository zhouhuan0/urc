package com.yks.urc.permitStat.bp.api;

import java.util.List;

/**
 * 更新冗余表：urc_user_permission_cache/urc_user_permit_stat
 * 
 * @author panyun@youkeshu.com
 * @date 2018年6月13日 下午2:46:24
 * 
 */
public interface IPermitStatBp {
	/**
	 * 更新用户功能权限冗余表
	 * 
	 * @author panyun@youkeshu.com
	 * @date 2018年6月13日 下午2:47:34
	 */
	void updateUserPermitCache(List<String> lstUserName);
}
