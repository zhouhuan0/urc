package com.yks.urc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.urc.authway.bp.api.AuthWayBp;
import com.yks.urc.dataauthorization.bp.api.DataAuthorization;
import com.alibaba.fastjson.JSONObject;
import com.yks.urc.entity.PlatformDO;
import com.yks.urc.entity.ShopSiteDO;
import com.yks.urc.entity.UserDO;
import com.yks.urc.exception.URCServiceException;
import com.yks.urc.fw.HttpUtility2;
import com.yks.urc.mapper.PlatformMapper;
import com.yks.urc.mapper.ShopSiteMapper;
import com.yks.urc.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IUserMapper;
import com.yks.urc.service.api.IUserService;
import com.yks.urc.user.bp.api.IUserBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.helper.Query;
import com.yks.urc.vo.helper.VoHelper;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserServiceImpl implements IUserService {
    private Logger logger=LoggerFactory.getLogger(this.getClass());

    @Autowired
    IUserBp userBp;
    @Autowired
    DataAuthorization dataAuthorization;
    @Autowired
    private AuthWayBp authWayBp;
    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private IUserValidateBp userValidateBp;
    @Autowired
    private IRoleMapper roleMapper;

    @Override
    @Transactional(rollbackFor=Exception.class)
    public ResultVO syncUserInfo(String operator) {
        ResultVO resultVO=new ResultVO();
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
    public ResultVO login(Map<String, String> map) {
        return userBp.login(map);
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
    @Transactional(rollbackFor=Exception.class)
    public ResultVO<PageResultVO> getUsersByUserInfo(String operator, UserVO userVO, String pageNumber, String pageData) {
        //首先要判断该用户是否是超级管理员或业务管理员
        if (!roleMapper.isAdminOrSuperAdmin(operator)) {
            return VoHelper.getResultVO(CommonMessageCodeEnum.FAIL.getCode(), "非管理员无法查看此数据");
        }
        return userBp.getUsersByUserInfo(operator, userVO, pageNumber, pageData);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public ResultVO<List<OmsPlatformVO>> getPlatformList(String operator) {
        ResultVO<List<OmsPlatformVO>> rslt=new ResultVO();
        try {
            if (StringUtility.isNullOrEmpty(operator)) {
                rslt.msg="操作人员为空 " + operator;
                rslt.state=CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
            rslt.data=dataAuthorization.getPlatformList(operator);
            if (rslt.data == null) {
                rslt.msg="Error,获取的平台为空 " + operator;
                rslt.state=CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
            rslt.msg="Success " + operator;
            rslt.state=CommonMessageCodeEnum.SUCCESS.getCode();
        } catch (Exception e) {
            logger.error("未知异常", e);
            throw new URCServiceException(CommonMessageCodeEnum.UNKOWN_ERROR.getCode(), "出现未知异常", e);
        } finally {
            return rslt;
        }
    }


    @Override
    @Transactional(rollbackFor=Exception.class)
    public ResultVO<List<OmsShopVO>> getShopList(String operator, String platform) {
        ResultVO<List<OmsShopVO>> rslt=new ResultVO();
        try {
            if (StringUtility.isNullOrEmpty(operator) || StringUtility.isNullOrEmpty(platform)) {
                return rslt;
            }
            rslt.data=dataAuthorization.getShopList(operator, platform);
            rslt.msg="Success";
            rslt.state=CommonMessageCodeEnum.SUCCESS.getCode();
            if (rslt.data == null) {
                rslt.msg="Error 无法找到此平台的店铺信息,或者无此平台," + operator;
                rslt.state=CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
        } catch (Exception e) {
            logger.error("未知异常", e);
            throw new URCServiceException(CommonMessageCodeEnum.UNKOWN_ERROR.getCode(), "出现未知异常", e);
        } finally {
            return rslt;
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public ResultVO<List<SysAuthWayVO>> getMyAuthWay(String operator) {
        ResultVO<List<SysAuthWayVO>> rslt=new ResultVO();
        try {
            if (StringUtility.isNullOrEmpty(operator)) {
                return rslt;
            }
            rslt.data=authWayBp.getMyAuthWay(operator);
            if (rslt.data == null) {
                rslt.msg="Failed ," + operator + "您不是管理员,没有授权权限";
                rslt.state=CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
            rslt.msg="成功, " + operator;
            rslt.state=CommonMessageCodeEnum.SUCCESS.getCode();
        } catch (Exception e) {
            logger.error("未知异常", e);
            throw new URCServiceException(CommonMessageCodeEnum.UNKOWN_ERROR.getCode(), "出现未知异常", e);
        } finally {
            return rslt;
        }
    }

    @Override
    public ResultVO<GetAllFuncPermitRespVO> getAllFuncPermit(String jsonStr) {
        try {
            JSONObject jsonObject=StringUtility.parseString(jsonStr);
            String operator=jsonObject.getString(StringConstant.operator);
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
        UserVO userVO=new UserVO();
        userVO.userName=userName;
        if (!roleMapper.isSuperAdminAccount(operator)) {
            userVO.createBy=operator;
        }
        Query query=new Query(userVO, pageNumber, pageData);
        List<UserVO> userList=userMapper.fuzzySearchUsersByUserName(query);
        long userCount=userMapper.fuzzySearchUsersByUserNameCount(query);
        PageResultVO pageResultVO=new PageResultVO(userList, userCount, pageData);
        return VoHelper.getSuccessResult(pageResultVO);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public ResultVO<List<UserVO>> getUserByUserName(String operator, UserVO userVO) {
        List<UserVO> userVOS=new ArrayList<>();
        UserVO userVO1=new UserVO();
        UserDO userDO=userMapper.getUserByUserName(userVO);
        if (userDO == null) {
            return VoHelper.getResultVO(CommonMessageCodeEnum.FAIL.getCode(), "用户不存在");
        }
        userVO1.userName=userDO.getUserName();
        userVOS.add(userVO1);
        return VoHelper.getSuccessResult(userVOS);
    }

    @Autowired
    private PlatformMapper platformMapper;
    @Autowired
    private ShopSiteMapper shopSiteMapper;

    @Override
    @Transactional(rollbackFor=Exception.class)
    public ResultVO<List<OmsPlatformVO>> getPlatformShopSite(String operator) {
        try {
            List<PlatformDO> platformDOS=platformMapper.selectAll();
            //装载平台volist
            List<OmsPlatformVO> omsPlatformVOS=new ArrayList<>();
            for (PlatformDO platformDO : platformDOS) {
                OmsPlatformVO omsPlatformVO=new OmsPlatformVO();
                omsPlatformVO.platformId=platformDO.getPlatformId();
                omsPlatformVO.platformName=platformDO.getPlatformName();

                List<ShopSiteDO> shopSiteDOS=shopSiteMapper.selectShopSiteByPlatformId(platformDO.getPlatformId());
                if (shopSiteDOS == null || shopSiteDOS.size() == 0) {
                    continue;
                } else {
                    //集合都必须先初识化
                    omsPlatformVO.lstShop=new ArrayList<>(shopSiteDOS.size());
                    for (ShopSiteDO shopSiteDO : shopSiteDOS) {
                        OmsShopVO omsShopVO=new OmsShopVO();
                        //针对速卖通的
                        omsShopVO.shopId=shopSiteDO.getSellerId();
                        omsShopVO.shopName=shopSiteDO.getShop();
                        //如果站点id为空,则list为空
                        if ("".equals(shopSiteDO.getSiteId())) {
                            omsShopVO.lstSite=null;
                        } else {
                            OmsSiteVO omsSiteVO=new OmsSiteVO();
                            omsSiteVO.siteId=shopSiteDO.getSiteId();
                            //如果站点名称为空,则吧站点id赋值给站点名称
                            if ("".equals(shopSiteDO.getSiteName())) {
                                omsSiteVO.siteName=omsSiteVO.siteId;
                            } else {
                                omsSiteVO.siteName=shopSiteDO.getSiteName();
                                omsShopVO.lstSite.add(omsSiteVO);
                            }
                        }
                        omsPlatformVO.lstShop.add(omsShopVO);
                    }
                    omsPlatformVOS.add(omsPlatformVO);
                }
            }
            return VoHelper.getSuccessResult(omsPlatformVOS);
        } catch (Exception e) {
            logger.error("获取数据异常:", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "未知异常");
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public ResultVO syncPlatform(String operator) {
        try {
            dataAuthorization.syncPlatform(operator);
            return VoHelper.getSuccessResult();
        } catch (Exception e) {
            logger.error("同步平台数据出错", e);
            return VoHelper.getErrorResult();
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public ResultVO syncShopSite(String operator) {
        try {
            dataAuthorization.syncShopSite(operator);
            return VoHelper.getSuccessResult();
        } catch (Exception e) {
            logger.error("同步账号站点数据出错", e);
            return VoHelper.getErrorResult();
        }
    }

    @Override
    public ResultVO resetPwdGetVerificationCode(String userName, String mobile) {
        if(StringUtility.isNullOrEmpty(userName)){
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(),"用户名不能为空");
        }
        if(StringUtility.isNullOrEmpty(mobile)){
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(),"手机号不能为空");
        }
        Map map=new HashMap(10);
        map.put("username", userName);
        map.put("mobile", mobile);
        map.put("get_code", "true");
        String response;
        try {
            response = HttpUtility2.postForm("https://userinfo.youkeshu.com/api/1.0/account/forgotpw", map, null);
        } catch (Exception e) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "获取验证码失败");
        }

        return VoHelper.getSuccessResult(response);
    }
    @Override
    public ResultVO resetPwdSubmit(String mobile, String new_password, String username, String code) {
        ResultVO rslt = new ResultVO();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("mobile", mobile);
        jsonObject.put("code", code);
        jsonObject.put("username", username);
        jsonObject.put("new_password", new_password);
        jsonObject.put("get_code", false);
        String requestBody=jsonObject.toString();
        Map<String, String> requestHeader=new HashMap();
        requestHeader.put("Content-Type", "application/json");
        try {
            String response = HttpUtility2.postString("https://userinfo.youkeshu.com/api/1.0/account/forgotpw", requestBody, requestHeader);
        }catch (Exception e)
        {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "重置密码失败");

        }
        rslt.msg="操作成功！";
        rslt.state=CommonMessageCodeEnum.SUCCESS.getCode();
        return rslt;
    }
}

