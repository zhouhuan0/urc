package com.yks.urc.cache.bp.api;

import java.util.List;

import com.yks.urc.vo.BizSysVO;
import com.yks.urc.vo.UserVO;

public interface ICacheBp {
	UserVO getUser(String userName);

	String getUserSysFuncJson(String userName, String sysKey);

	String getUserSysFuncVersion(String userName, String sysKey);

	List<String> getUserSysKey(String userName);

	void insertUser(UserVO u);

	void insertUserSysFuncJson(String userName, String sysKey, String funcJson);

	void insertUserSysFuncVersion(String userName, String sysKey, String funcVersion);

	void insertUserSysKey(String userName, List<String> lst);
}
