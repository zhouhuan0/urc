package com.yks.urc.user.bp.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.entity.UserDO;
import com.yks.urc.entity.UserInfo;
import com.yks.urc.entity.UserLoginLogDO;
import com.yks.urc.fw.HttpUtility;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.ldap.bp.api.ILdapBp;
import com.yks.urc.lock.DistributedReentrantLock;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IUserLoginLogMapper;
import com.yks.urc.mapper.IUserMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.user.bp.api.IUserBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.VoHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class UserBpImpl implements IUserBp {
	private static Logger logger = LoggerFactory.getLogger(UserBpImpl.class);

	@Autowired
	IUserValidateBp userValidateBp;

	@Autowired
	ILdapBp ldapBpImpl;

	@Autowired
	IUserLoginLogMapper userLoginLogMapper;
	/**
	 * token 请求地址
	 */
	@Value("${userInfo.token}")
	private static String GET_TOKEN;
	/**
	 * 获取UserInfo信息地址
	 */
	@Value("${userInfo.address}")
	private static String USER_INFO_ADDRESS;
	@Value("${userInfo.username}")
	private static String username;
	@Value("${userInfo.password}")
	private static String password;

	@Autowired
	private IUserMapper userMapper;
	@Autowired
	private IRoleMapper roleMapper;
	@Autowired
	private IUserRoleMapper userRoleMapper;

	@Autowired
	private IOperationBp operationBp;

	@Autowired
	private ICacheBp cacheBp;

	/**
	 * 同步UserInfo数据
	 *
	 * @Author: linwanxian@youkeshu.com
	 * @Date: 2018/6/8 15:29
	 */
	DistributedReentrantLock lock = new DistributedReentrantLock("SynUserFromUserInfo");

	@Transactional(rollbackFor = Exception.class)
	public void SynUserFromUserInfo(String username) {
		if (lock.tryLock()) {
			List<UserInfo> userInfoList = this.getUserInfo();
			UserDO userDo = new UserDO();
			for (UserInfo user : userInfoList) {
				userDo.setUserName(user.username);
				userDo.setDingUserId(user.ding_userid);
				userDo.setCreateBy(username);
				userDo.setModifiedBy(username);
				try {
					userDo.setActiveTime(StringUtility.stringToDate(user.date_joined, "yyyy-MM-dd HH:mm:ss"));
					// 1 表示启用,0表示禁用
					if ("66050".equals(user.userAccountControl)) {
						userDo.setIsActive(0);
					} else if ("66048".equals(user.userAccountControl) || "512".equals(user.userAccountControl)) {
						userDo.setIsActive(1);
					} else {
						userDo.setIsActive(Integer.parseInt(user.userAccountControl));
					}
					// 传入手动同步的创建人员
					userDo.setCreateBy(username);
					userDo.setModifiedBy(username);
					List<UserDO> userDoList = new ArrayList<>();
					userDoList.add(userDo);
					// 先清理数据表
					userMapper.deleteUrcUser();
					logger.info("清理完成,开始同步");
					userMapper.insertBatchUser(userDoList);
					operationBp.addLog(this.getClass().getName(), "同步userInfo数据成功..", null);
				} catch (Exception e) {
					operationBp.addLog(this.getClass().getName(), "同步userInfo数据出错..", e);
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		} else {
			if ("system".equals(username)) {
				// 手动触发正在执行..记录日志
				operationBp.addLog(this.getClass().getName(), "手动触发正在执行..", null);
			} else {
				// 定时任务触发正在执行..记录日志
				operationBp.addLog(this.getClass().getName(), "定时任务正在执行..", null);
			}
			logger.info("同步userInfo数据正在执行...,");

		}

	}

	/**
	 * 搜索用户
	 *
	 * @param userVO
	 * @param pageNumber
	 * @param pageData
	 * @return ResultVO
	 * @Author linwanxian@youkeshu.com
	 * @Date 2018/6/11 10:28
	 */
	public ResultVO<PageResultVO> getUsersByUserInfo(UserVO userVO, int pageNumber, int pageData) {
		userMapper.getUsersByUserInfo(userVO, pageNumber, pageData);
		// 1.首先查询出所有数据,将userDo的数据组装到uservo
		List<UserVO> userVOList = new ArrayList<>();
		List<UserDO> userDOList = userMapper.getUsersByUserInfo(userVO, pageNumber, pageData);
		for (UserDO userDO : userDOList) {
			userVO.userName = userDO.getUserName();
			userVO.personName = userDO.getPerson().getPersonName();
			userVO.activeTime = userDO.getActiveTime().toString();
			// 1 为启用
			userVO.isActive = userDO.getIsActive() == 1 ? true : false;
		}

		// 2.将拿到的用户名再去获取角色名称
		RoleVO roleVO = new RoleVO();
		for (UserDO user : userDOList) {
			String username = roleMapper.selectRoleName(user.getUserName());
			roleVO.roleName = username;
		}

		// 3.将拿到的角色对象组装到uservo里面
		List<RoleVO> list = new ArrayList();
		list.add(roleVO);
		userVO.roles = list;

		// 4.组装userVo
		userVOList.add(userVO);
		// 获取总条数
		int  total = userMapper.getUsersByUserInfoCount(userVO);
		PageResultVO pageResultVO = new PageResultVO(userVOList, total, pageData);
		return VoHelper.getSuccessResult(pageResultVO);
	}

	/**
	 * 获取system
	 * 
	 * @param userName
	 * @param sysKey
	 * @param ticket
	 * @return ResultVO<UserSysVO>
	 * @Author linwanxian@youkeshu.com
	 * @Date 2018/6/12 9:34
	 */
	public ResultVO<UserSysVO> getSysKeyByUserName(String userName, String sysKey, String ticket) {
		List<String> userRoleDOS = userRoleMapper.getSysKeyByUser(userName);
		try {

			// 功能版本的生成逻辑 根据userName/syskey取context,进行MD5;
			// 获取功能权限
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 请求userInfo
	 *
	 * @Author linwanxian@youkeshu.com
	 * @Date 2018/6/8 20:43
	 */
	public List getUserInfo() {
		List<UserInfo> dingUserList = null;
		// 1.请求token
		JSONObject object = new JSONObject();
		object.put("username", username);
		object.put("password", password);
		try {
			String accessToken = HttpUtility.sendPost(GET_TOKEN, object.toJSONString());
			logger.info("获取token");
			// 将拿到的string 转为json
			JSONObject jsonToken = StringUtility.parseString(accessToken);
			String token = jsonToken.getString("token");
			// 2.只调用UserInfo接口，同步UserInfo数据
			String userInfo = HttpUtility.httpGet(USER_INFO_ADDRESS + token);
			// 解析json数组
			logger.info("获取userInfo");
			dingUserList = StringUtility.jsonToList(userInfo, UserInfo.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return dingUserList;
	}

	public static void main(String[] args) {
		UserBpImpl userBp = new UserBpImpl();
		userBp.SynUserFromUserInfo("lwx");
	}

	public ResultVO login(UserVO authUser) {
		try {
			LoginRespVO resp = new LoginRespVO();
			long startTime = System.currentTimeMillis();
			boolean blnOk = ldapBpImpl.validateUser(authUser.userName, authUser.pwd);
			long endTime = System.currentTimeMillis();

			UserLoginLogDO loginLog = new UserLoginLogDO();
			loginLog.userName = authUser.userName;
			loginLog.ip = authUser.ip;
			loginLog.ldapCost = endTime - startTime;
			loginLog.loginSuccess = blnOk ? 1 : 0;
			loginLog.remark = String.format("PWD:%s", authUser.pwd);
			loginLog.loginTime = new Date();
			this.insertLoginLog(loginLog);
			resp.userName = authUser.userName;
			if (blnOk) {
				// 先从缓存取
				List<String> lstSysKey = cacheBp.getUserSysKey(resp.userName);
				if (lstSysKey == null) {
					resp.sysKey = userRoleMapper.getSysKeyByUser(authUser.userName);
					if (resp.sysKey == null)
						resp.sysKey = new ArrayList<>();
					cacheBp.insertUserSysKey(resp.userName, resp.sysKey);
				} else {
					resp.sysKey = lstSysKey;
				}
				resp.ticket = userValidateBp.createTicket(authUser.userName, authUser.ip);
			}
			return VoHelper.getSuccessResult(resp, blnOk ? "000001" : "000000", null);
		} catch (Exception ex) {
			return VoHelper.getSuccessResult(null, "000000", "login error");
		}
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
