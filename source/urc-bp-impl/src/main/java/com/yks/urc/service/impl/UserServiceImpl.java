package com.yks.urc.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yks.urc.bp.impl.UserBp;
import com.yks.urc.entity.UserLoginLogDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.ldap.bp.api.ILdapBp;
import com.yks.urc.ldap.bp.impl.LdapBpImpl;
import com.yks.urc.mapper.IUserLoginLogMapper;
import com.yks.urc.service.api.IUserService;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;
import com.yks.urc.vo.helper.VoHelper;

@Component
public class UserServiceImpl implements IUserService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	UserBp userBp;

	@Autowired
	ILdapBp ldapBpImpl;
	
	@Autowired
	IUserLoginLogMapper userLoginLogMapper;

	@Override
	public ResultVO syncUserInfo(UserVO curUser) {
		userBp.SynUserFromUserInfo(curUser.userName);
		ResultVO rslt = VoHelper.getSuccessResult();
		rslt.msg = "Success " + curUser.userName;
		return rslt;
	}

	@Override
	public ResultVO login(UserVO curUser, UserVO authUser) {
		long startTime = System.currentTimeMillis();
		boolean blnOk = ldapBpImpl.validateUser(authUser.userName, authUser.pwd);
		long endTime = System.currentTimeMillis();

		UserLoginLogDO loginLog = new UserLoginLogDO();
		loginLog.userName = authUser.userName;
		loginLog.ip = curUser.ip;
		loginLog.ldapCost = endTime - startTime;
		loginLog.loginSuccess = blnOk ? 1 : 0;
		loginLog.remark = String.format("PWD:%s", authUser.pwd);
		loginLog.loginTime = new Date();
		this.insertLoginLog(loginLog);
		return VoHelper.getSuccessResult(null, blnOk ? "00001" : "00000", null);
	}

	ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);

	/**
	 * 登录日志入库
	 * 
	 * @param loginLog
	 * @author panyun@youkeshu.com
	 * @date 2018年6月6日 下午2:20:46
	 */
	private void insertLoginLog(UserLoginLogDO loginLog) {
		fixedThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				try {
					loginLog.createTime = new Date();
					loginLog.modifiedTime = new Date();
					userLoginLogMapper.insertUserLoginLog(loginLog);
				} catch (Exception ex) {
					logger.error(StringUtility.toJSONString_NoException(loginLog), ex);
				}
			}
		});
	}
}
