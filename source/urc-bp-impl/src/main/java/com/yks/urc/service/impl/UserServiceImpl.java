package com.yks.urc.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.urc.authway.bp.api.AuthWayBp;
import com.yks.urc.dataauthorization.bp.api.DataAuthorization;
import com.alibaba.fastjson.JSONObject;
import com.yks.urc.entity.UserDO;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.exception.URCServiceException;
import com.yks.urc.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IUserMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.service.api.IUserService;
import com.yks.urc.user.bp.api.IUserBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.helper.Query;
import com.yks.urc.vo.helper.VoHelper;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private IUserValidateBp userValidateBp;
    @Autowired
    private IRoleMapper roleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO syncUserInfo(String operator) {
        ResultVO resultVO= new ResultVO();
        try {
            resultVO=userBp.SynUserFromUserInfo(operator);
        } catch (Exception e) {
           logger.error("同步任务异常" + e.getMessage());
            return VoHelper.getErrorResult();
        } finally {
            return resultVO;
        }
    }

    @Override
    public ResultVO login(UserVO authUser) {
        return userBp.login(authUser);
    }

    @Override
    public ResultVO logout(String jsonStr) {
        return userBp.logout(jsonStr);
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
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<PageResultVO> getUsersByUserInfo(String operator, UserVO userVO, String pageNumber, String pageData) {
        return userBp.getUsersByUserInfo(operator, userVO, pageNumber, pageData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<List<OmsPlatformVO>> getPlatformList(String operator) {
        ResultVO<List<OmsPlatformVO>> rslt = new ResultVO();
        try {
            if (StringUtility.isNullOrEmpty(operator)) {
                rslt.msg = "操作人员为空 " + operator;
                rslt.state=CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
            rslt.data = dataAuthorization.getPlatformList(operator);
            if (rslt.data == null){
                rslt.msg = "Error,获取的平台为空 " + operator;
                rslt.state=CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
            rslt.msg = "Success " + operator;
            rslt.state=CommonMessageCodeEnum.SUCCESS.getCode();
        } catch (Exception e) {
            logger.error("未知异常",e);
            throw  new URCServiceException(CommonMessageCodeEnum.UNKOWN_ERROR.getCode(),"出现未知异常",e);
        } finally {
            return rslt;
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<List<OmsShopVO>> getShopList(String operator, String platform) {
        ResultVO<List<OmsShopVO>> rslt = new ResultVO();
        try {
            if (StringUtility.isNullOrEmpty(operator) || StringUtility.isNullOrEmpty(platform)) {
                return rslt;
            }
            rslt.data = dataAuthorization.getShopList(operator, platform);
            rslt.msg = "Success " + operator;
            rslt.state=CommonMessageCodeEnum.SUCCESS.getCode();
            if (rslt.data == null) {
                rslt.msg = "Error 无法找到此平台的店铺信息,或者无此平台," + operator;
                rslt.state=CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
        } catch (Exception e) {
            logger.error("未知异常",e);
            throw  new URCServiceException(CommonMessageCodeEnum.UNKOWN_ERROR.getCode(),"出现未知异常",e);
        } finally {
            return rslt;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<List<SysAuthWayVO>> getMyAuthWay(String operator) {
        ResultVO<List<SysAuthWayVO>> rslt = new ResultVO();
        try {
            if (StringUtility.isNullOrEmpty(operator)) {
                return rslt;
            }
            rslt.data = authWayBp.getMyAuthWay(operator);
            if (rslt.data == null) {
                rslt.msg = "Failed ," + operator + "您不是管理员,没有授权权限";
                rslt.state=CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
            rslt.msg = "成功, " + operator;
            rslt.state=CommonMessageCodeEnum.SUCCESS.getCode();
        } catch (Exception e) {
            logger.error("未知异常",e);
          throw  new URCServiceException(CommonMessageCodeEnum.UNKOWN_ERROR.getCode(),"出现未知异常",e);
        } finally {
            return rslt;
        }
    }

    @Override
    public ResultVO<GetAllFuncPermitRespVO> getAllFuncPermit(String jsonStr) {
        try {
            JSONObject jsonObject = StringUtility.parseString(jsonStr);
            String operator = jsonObject.getString(StringConstant.operator);
            return userBp.getAllFuncPermit(operator);
        } catch (Exception ex) {
            logger.error(String.format("getAllFuncPermit:%s", jsonStr), ex);
            return VoHelper.getErrorResult();
        }
    }


    @Override
    public ResultVO funcPermitValidate(Map<String, String> map) {
        return userValidateBp.funcPermitValidate(map);
    }



    @Override
    public ResultVO fuzzySearchUsersByUserName(String pageNumber, String pageData, String userName, String operator) {
        UserVO userVO = new UserVO();
        userVO.userName = userName;
        if (!roleMapper.isSuperAdminAccount(operator)) {
            userVO.createBy = operator;
        }
        Query query = new Query(userVO, pageNumber, pageData);
        List<UserVO> userList = userMapper.fuzzySearchUsersByUserName(query);
        long userCount = userMapper.fuzzySearchUsersByUserNameCount(query);
        PageResultVO pageResultVO = new PageResultVO(userList, userCount, pageData);
        return VoHelper.getSuccessResult(pageResultVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<List<UserVO>> getUserByUserName(String operator, UserVO userVO) {
        List<UserVO> userVOS = new ArrayList<>();
        UserVO userVO1 = new UserVO();
        UserDO userDO = userMapper.getUserByUserName(userVO);
        if (userDO == null){
            return VoHelper.getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(),"用户不存在");
        }
        userVO1.userName = userDO.getUserName();
        userVOS.add(userVO1);
        return VoHelper.getSuccessResult(userVOS);
    }
}
