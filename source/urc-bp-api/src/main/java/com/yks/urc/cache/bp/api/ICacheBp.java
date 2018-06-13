package com.yks.urc.cache.bp.api;

import java.util.List;

import com.yks.urc.entity.UserPermissionCacheDO;
import com.yks.urc.vo.UserVO;

public interface ICacheBp {
	UserVO getUser(String userName);

	List<UserPermissionCacheDO> getUserFunc(String userName);

	List<String> getUserSysKey(String userName);

	void insertUser(UserVO u);

	void insertUserFunc(String userName, List<UserPermissionCacheDO> lstPermitCache);

	void removeUserFunc(String userName);

	void insertUserSysKey(String userName, List<String> lst);

	void removeUserSysKey(String userName);
}
