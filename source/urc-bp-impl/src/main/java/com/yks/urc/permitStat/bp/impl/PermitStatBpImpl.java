package com.yks.urc.permitStat.bp.impl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IUserPermissionCacheMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;

@Component
public class PermitStatBpImpl implements IPermitStatBp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);

	@Override
	public void updateUserPermitCache(List<String> lstUserName) {
		if (lstUserName == null || lstUserName.size() == 0)
			return;
		fixedThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				for (String userName : lstUserName) {
					updateUserPermitCache(userName);
				}
			}
		});
	}

	@Autowired
	private IUserRoleMapper userRoleMapper;
	@Autowired
	IRoleMapper roleMapper;
	@Autowired
	private IUserPermissionCacheMapper permissionCacheMapper;
	@Autowired
	private IUserValidateBp userValidateBp;

	private void updateUserPermitCache(String userName) {
		try {
			// 获取用户所有的sysKey
			List<String> lstSysKey = userRoleMapper.getSysKeyByUser(userName);
			if (lstSysKey == null || lstSysKey.size() == 0) {

			}

			permissionCacheMapper.deletePermitCacheByUser(userName);
			permissionCacheMapper.deletePermitStatByUser(userName);

			// 获取用户所有sys的功能权限json
			for (String sysKey : lstSysKey) {
				List<String> lstFuncJson = roleMapper.getFuncJsonByUserAndSysKey(userName, sysKey);
				userValidateBp.getFuncJsonByUserAndSysKey(userName, sysKey);
			}

			// 更新 urc_user_permission_cache表

			// 更新 urc_user_permit_stat 表

			// 更新缓存
		} catch (Exception ex) {
			logger.error(String.format("updateUserPermitCache:%s", userName), ex);
		}
	}
}
