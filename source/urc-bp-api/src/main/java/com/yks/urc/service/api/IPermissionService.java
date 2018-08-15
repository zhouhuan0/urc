package com.yks.urc.service.api;


import com.yks.urc.vo.ResultVO;

public interface IPermissionService {

	ResultVO importSysPermit(String jsonStr);
	
	
	/**
	 * 获取指定用户可授权给其它角色的功能权限
	 * @param operator
	 * @return
	 */
	ResultVO getUserAuthorizablePermission(String operator);

	ResultVO getUserPermissionList(String jsonStr);
	/**
	 *  更新缓存API前缀
	 * @param
	 * @return
	 * @Author lwx
	 * @Date 2018/8/15 10:18
	 */
	ResultVO updateApiPrefixCache();
}
