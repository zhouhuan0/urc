package com.yks.urc.userValidate.bp.api;

import java.util.List;

public interface IUserValidateBp {
	/**
	 * 根据username/syskey获取角色功能权限json list
	 * 
	 * @param userName
	 * @param sysKey
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月12日 下午3:44:53
	 */
	List<String> getFuncJsonLstByUserAndSysKey(String userName, String sysKey);

	/**
	 * 根据username/syskey获取角色功能权限json(合并后的json)
	 * 
	 * @param userName
	 * @param sysKey
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月12日 下午3:44:55
	 */
	String getFuncJsonByUserAndSysKey(String userName, String sysKey);
}
