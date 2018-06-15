package com.yks.urc.service.impl;

import java.util.List;
import java.util.Map;

import com.yks.urc.authway.bp.api.AuthWayBp;
import com.yks.urc.dataauthorization.bp.api.DataAuthorization;
import com.yks.urc.dataauthorization.bp.impl.DataAuthorizationImpl;
import com.alibaba.fastjson.JSONObject;
import com.yks.urc.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yks.urc.entity.DataRuleDO;
import com.yks.urc.entity.UserDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.mapper.IUserMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.service.api.IUserService;
import com.yks.urc.user.bp.api.IUserBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.userValidate.bp.impl.UserValidateBp;
import com.yks.urc.vo.helper.VoHelper;

@Component
public class UserServiceImpl implements IUserService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IUserBp userBp;
    @Autowired
    DataAuthorization dataAuthorization;
    @Autowired
    private AuthWayBp authWayBp;
	@Autowired
	private IUserRoleMapper userRoleMapper;
	private IUserMapper userMapper;
	@Autowired
	private IUserValidateBp userValidateBp;

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

	/*
	 * public ResultVO queryUserDataByRuleId(DataRuleDO ruleDO) { List<UserDO>
	 * userList = userMapper.queryUserDataByRuleId(ruleDO); if (userList != null &&
	 * userList.size() > 0) { return VoHelper.getSuccessResult(userList); } return
	 * VoHelper.getErrorResult(); }
	 *
	 * public ResultVO queryUserNoDataByRuleId(DataRuleDO ruleDO) { List<UserDO>
	 * userList = userMapper.queryUserNoDataByRuleId(ruleDO); if (userList != null
	 * && userList.size() > 0) { return VoHelper.getSuccessResult(userList); }
	 * return VoHelper.getErrorResult(); }
	 */


    @Override
    public ResultVO<PageResultVO> getUsersByUserInfo(String operator,UserVO userVO, int pageNumber, int pageData) {
        return userBp.getUsersByUserInfo(operator,userVO, pageNumber, pageData);
    }

	@Override
	public ResultVO<List<OmsPlatformVO>> getPlatformList(String operator) {
		ResultVO rslt = null;
		try {
			rslt.data = dataAuthorization.getPlatformList(operator);
			rslt.msg = "Success " + operator;
			rslt = VoHelper.getSuccessResult(rslt.data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return rslt;
		}
	}


    @Override
    public ResultVO<List<OmsAccountVO>> getShopList(String operator, String platform) {
        ResultVO rslt = null;
        try {
            rslt.data = dataAuthorization.getShopList(operator, platform);
            rslt.msg = "Success " + operator;
            rslt = VoHelper.getSuccessResult(rslt.data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return rslt;
        }
    }

    @Override
    public ResultVO<List<SysAuthWayVO>> getMyAuthWay(String operator) {
        ResultVO rslt = null;
        try {
            rslt.data = authWayBp.getMyAuthWay(operator);
            rslt.msg = "Success " + operator;
            rslt = VoHelper.getSuccessResult(rslt.data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return rslt;
        }
    }

	@Override
	public String getAllFuncPermit(String jsonStr) {
		try {
			JSONObject jsonObject = StringUtility.parseString(jsonStr);
			String operator = jsonObject.getString(StringConstant.operator);
			ResultVO<List<UserSysVO>> rslt = userBp.getAllFuncPermit(operator);
			return StringUtility.toJSONString_NoException(rslt);
		} catch (Exception ex) {
			logger.error(String.format("getAllFuncPermit:%s", jsonStr), ex);
			return StringUtility.toJSONString_NoException(VoHelper.getErrorResult());
		}
	}


	@Override
	public String funcPermitValidate(Map<String, String> map) {
			ResultVO rslt = userValidateBp.funcPermitValidate(map);
			return StringUtility.toJSONString_NoException(rslt);
	}
}
