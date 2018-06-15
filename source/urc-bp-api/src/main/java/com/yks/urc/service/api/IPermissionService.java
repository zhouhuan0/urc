package com.yks.urc.service.api;


import com.yks.urc.vo.ResultVO;

public interface IPermissionService {

	String importSysPermit(String jsonStr);
	
	
	/**
	 * 获取指定用户可授权给其它角色的功能权限
	 * @param operator
	 * @return
	 */
	ResultVO getUserAuthorizablePermission(String operator);

}
