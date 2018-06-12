package com.yks.urc.userValidate.bp.api;

import java.util.List;

public interface IUserValidateBp {
	List<String> getFuncJsonByUserAndSysKey(String userName, String sysKey);
}
