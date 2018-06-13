package com.yks.urc.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yks.urc.entity.DataRuleDO;
import com.yks.urc.entity.UserDO;
import com.yks.urc.entity.UserLoginLogDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.ldap.bp.api.ILdapBp;
import com.yks.urc.ldap.bp.impl.LdapBpImpl;
import com.yks.urc.mapper.IUserLoginLogMapper;
import com.yks.urc.mapper.IUserMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.service.api.IUserService;
import com.yks.urc.user.bp.api.IUserBp;
import com.yks.urc.user.bp.impl.UserBpImpl;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.LoginRespVO;
import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;
import com.yks.urc.vo.helper.VoHelper;

@Component
public class UserServiceImpl implements IUserService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IUserBp userBp;
	
	@Autowired
	private IUserRoleMapper userRoleMapper;
	
	
	@Autowired
	private IUserMapper userMapper;
	

	@Override
	public ResultVO syncUserInfo(UserVO curUser) {
		ResultVO rslt = null;
		try {
			userBp.SynUserFromUserInfo(curUser.userName);
			rslt = VoHelper.getSuccessResult();
			rslt.msg = "Success " + curUser.userName;
		} catch (Exception e) {
			rslt = VoHelper.getErrorResult();
			rslt.msg = "Error" + curUser.userName;
		} finally {
			return rslt;
		}
	}

	@Override
	public ResultVO login(UserVO authUser) {
		return userBp.login(authUser);
	}

	
	public ResultVO queryUserDataByRuleId(DataRuleDO ruleDO) {
		List<UserDO> userList=userMapper.queryUserDataByRuleId(ruleDO);
		if(userList!=null&&userList.size()>0){
			return VoHelper.getSuccessResult(userList);
		}
		return VoHelper.getErrorResult();
	}
	
	public ResultVO queryUserNoDataByRuleId(DataRuleDO ruleDO) {
		List<UserDO> userList=userMapper.queryUserNoDataByRuleId(ruleDO);
		if(userList!=null&&userList.size()>0){
			return VoHelper.getSuccessResult(userList);
		}
		return VoHelper.getErrorResult();
	}

	@Override
	public ResultVO<PageResultVO> getUsersByUserInfo(UserVO userVO, int pageNumber, int pageData) {
		return userBp.getUsersByUserInfo(userVO, pageNumber, pageData);
	}

}
