package com.yks.urc.user.bp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.comparator.impl.UserSysVOComparator;
import com.yks.urc.config.bp.api.IConfigBp;
import com.yks.urc.constant.UrcConstant;
import com.yks.urc.entity.*;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.exception.URCServiceException;
import com.yks.urc.fw.*;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.ldap.bp.api.ILdapBp;
import com.yks.urc.lock.bp.api.ILockBp;
import com.yks.urc.mapper.*;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.user.bp.api.IUserBp;
import com.yks.urc.user.bp.api.IUserLogBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.Query;
import com.yks.urc.vo.helper.VoHelper;
import jdk.nashorn.internal.scripts.JS;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import sun.security.krb5.Config;

import java.util.*;

@Component
public class UserBpImpl implements IUserBp {
    private static Logger logger = LoggerFactory.getLogger(UserBpImpl.class);

    @Autowired
    IUserValidateBp userValidateBp;

    @Autowired
    ILdapBp ldapBpImpl;

    @Autowired
    IUserLoginLogMapper userLoginLogMapper;

    @Autowired
    private UserTicketMapper userTicketMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private IConfigBp configBp;
    @Autowired
    private ISerializeBp serializeBp;
    /**
     * token ????????????
     */
    @Value("${userInfo.token}")
    private String GET_TOKEN;
    /**
     * ??????UserInfo????????????
     */
    @Value("${userInfo.address}")
    private String USER_INFO_ADDRESS;
    @Value("${userInfo.username}")
    private String username;
    @Value("${userInfo.password}")
    private String password;

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

    @Autowired
    private IPermitStatBp permitStatBp;

    @Autowired
    private ILockBp lockBp;

    /**
     * ??????UserInfo?????? lock name
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2020-08-06 14:23
     */
    private String lkName = "SynUserFromUserInfo";

    @Override
    public ResultVO SynUserFromUserInfo(String username) {
        if (!lockBp.tryLock(lkName)) {
            logger.info("??????userInfo??????????????????...,");
            // ??????????????????????????????..????????????
            operationBp.addLog(this.getClass().getName(), "????????????..", null);
            return VoHelper.getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), "????????????..");
        }

        try {
           /* List<UserInfo> userInfoList = this.getUserInfo();
            if (userInfoList == null || userInfoList.size() == 0) {
                logger.error(String.format("??????userInfo ????????????"));
                operationBp.addLog(this.getClass().getName(), "??????userInfo ????????????", null);
                throw new URCBizException("??????userInfo ????????????", ErrorCode.E_000000);
            }*/
            return BeanProvider.getBean(IUserBp.class).updateAllUserInfoNew(username);
        } catch (Exception e) {
            throw new URCServiceException(CommonMessageCodeEnum.FAIL.getCode(), "??????userInfo????????????..", e);
        } finally {
            lockBp.unlock(lkName);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVO updateAllUserInfo(List<UserInfo> userInfoList) throws Exception {
        if (CollectionUtils.isEmpty(userInfoList)) {
            return VoHelper.getFail("????????????");
        }
        List<UserDO> userDoList = new ArrayList<>();

        // ??????????????????
        userMapper.deleteUrcUser();
        logger.info("????????????,????????????");
        for (UserInfo user : userInfoList) {
            UserDO userDo = new UserDO();
            userDo.setUserName(user.username);
            userDo.setChineseName(user.chinese_name);
            userDo.setMobile(user.mobile);
            userDo.setDingUserId(user.ding_userid);
            userDo.setCreateBy(username);
            userDo.setModifiedBy(username);
            userDo.setCreateTime(StringUtility.getDateTimeNow());
            userDo.setModifiedTime(StringUtility.getDateTimeNow());
            userDo.setActiveTime(StringUtility.stringToDate(user.date_joined, "yyyy-MM-dd HH:mm:ss"));
            // 1 ????????????,0????????????
            if ("66050".equals(user.ad_control_number)) {
                userDo.setIsActive(0);
            } else {
                userDo.setIsActive(1);
            }
            // ?????????????????????????????????
            userDo.setCreateBy(username);
            userDo.setModifiedBy(username);
            userDoList.add(userDo);
            //????????????1000???, ??????????????????,?????????list??????,???????????????
            if (userDoList.size() >= 1000) {
                userMapper.insertBatchUser(userDoList);
                userDoList.clear();
            }
        }
        //???????????????,????????????????????????,???????????????
        if (userDoList.size() != 0) {
            userMapper.insertBatchUser(userDoList);
        }
        operationBp.addLog(this.getClass().getName(), "??????userInfo????????????..", null);
        return VoHelper.getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), "??????userInfo????????????..");
    }

    @Override
    public ResultVO getAllFuncPermitForOtherSystem(String operator, List<String> sysKeys) {
        // ???????????????
        GetAllFuncPermitRespVO permitCache = cacheBp.getUserFunc(operator, sysKeys,UrcConstant.SysType.FBA);
        if (permitCache == null) {
            // ???DB???,???????????????
            permitCache = permitStatBp.updateUserPermitCache(operator);
            if (permitCache.lstUserSysVO != null && permitCache.lstUserSysVO.size() > 0) {
                permitCache.lstSysRoot = new ArrayList<>(permitCache.lstUserSysVO.size());
                Collections.sort(permitCache.lstUserSysVO, myUserSysVOComparator);
                for (UserSysVO us : permitCache.lstUserSysVO) {
                    //????????????
                    if(!StringUtility.stringEqualsIgnoreCaseObj(permissionMapper.getSysType(us.sysKey),UrcConstant.SysType.FBA)){
                        continue;
                    }
                    permitCache.lstSysRoot.add(us.context);
                }
                permitCache.lstUserSysVO = null;
            }
        }

        if (permitCache != null) {
            permitCache.lstUserSysVO = null;
            if (permitCache.lstSysRoot == null) {
                permitCache.lstSysRoot = new ArrayList<>();
            }
            if (permitCache.funcVersion == null) {
                permitCache.funcVersion = StringUtility.Empty;
            }
        }
        return VoHelper.getSuccessResult(permitCache);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateAllUserInfoNew(String operator) {
        List<UserDO> userDoList = new ArrayList<>();
        logger.info("????????????");
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Content-Type","application/json");
        headMap.put("yksToken", configBp.getString("yksToken","NjAzNGY0NWU2NDI2NTo3NTE5ZTVkZjMxZmQxMjk5MDUyNzgwYWM4YmMwODIwNw=="));
        Date dateTimeNow = StringUtility.getDateTimeNow();
        JSONObject paramBody = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("type","java");
        data.put("modifiedTime",StringUtility.convertToDate(configBp.getStringFromDb("synUserInfoPoint","2020-01-01 00:00:00"),dateTimeNow).getTime());
        paramBody.put("data",data);
        String responseString = HttpUtility.postHasHeaders(configBp.getString("getUserInfoUrl","http://ykshr.kokoerp.com/openapi/employee/getList"), headMap, paramBody.toString(), "utf-8");
        if(StringUtility.isNullOrEmpty(responseString)){
            return VoHelper.getFail("????????????,???????????????");
        }
        JSONObject jsonObject = JSON.parseObject(responseString);
        if(!StringUtility.stringEqualsIgnoreCase(CommonMessageCodeEnum.SUCCESS.getCode(),jsonObject.getString("state"))){
            return VoHelper.getFail(jsonObject.getString("msg"));
        }
        JSONObject objectJSONObject = jsonObject.getJSONObject("data");
        JSONArray list = objectJSONObject.getJSONArray("list");
        //??????????????????????????????????????????
        if(list == null || list.isEmpty() ){
            return VoHelper.getSuccessResult();
        }

        for (Object o : list) {
            JSONObject object = (JSONObject) o;
            String avatar = object.getString("avatar");
            String chineseName = object.getString("chineseName");
            String dingUserId = object.getString("dingUserId");
            String mobile = object.getString("mobile");
            String userName = object.getString("userName");
            String activeTime = object.getString("activeTime");
            String isActive = object.getString("isActive");
            if(StringUtility.isNullOrEmpty(userName)){
                continue;
            }
            UserDO userDo = new UserDO();
            userDo.setUserName(userName);
            userDo.setChineseName(chineseName);
            userDo.setMobile(mobile);
            userDo.setDingUserId(dingUserId);
            userDo.setCreateBy(operator);
            userDo.setModifiedBy(operator);
            userDo.setCreateTime(StringUtility.getDateTimeNow());
            userDo.setModifiedTime(StringUtility.getDateTimeNow());
            userDo.setActiveTime(StringUtility.isNullOrEmpty(activeTime) || activeTime.length()< 13 ? new Date() : new Date(Long.valueOf(activeTime)));
            userDo.setAvatar(avatar);
            // 1?????????2??????
            if (StringUtility.stringEqualsIgnoreCase(isActive,"2")) {
                userDo.setIsActive(0);
            } else {
                userDo.setIsActive(1);
            }
            userDoList.add(userDo);
            //????????????1000???, ??????????????????,?????????list??????,???????????????
            if (userDoList.size() >= 1000) {
                userMapper.insertOrUpdateBatchUser(userDoList);
                userDoList.clear();
            }
        }
        //???????????????,????????????????????????,???????????????
        if (userDoList.size() != 0) {
            userMapper.insertOrUpdateBatchUser(userDoList);
        }
        operationBp.addLog(this.getClass().getName(), "??????userInfo????????????..", null);
        //configBp.update2Db("synUserInfoPoint", DateUtil.formatDate(dateTimeNow, DateUtil.YYYY_MM_DD_HH_MM_SS));
        return VoHelper.getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), "??????userInfo????????????..");
    }

    /**
     * ???????????? , ????????????????????? ?????????????????????, ???????????????????????????
     *
     * @param userVO
     * @param pageNumber
     * @param pageData
     * @return ResultVO
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/11 10:28
     */
    @Override
    public ResultVO<PageResultVO> getUsersByUserInfo(String operator, UserVO userVO, String pageNumber, String pageData) {
        //?????????UserVo ???username
        List<String> strings = new ArrayList<>();
        if (userVO != null) {
            String allUserName = userVO.userName;
            //?????? , ???????????????,????????????,??????list
            if (allUserName.contains(",")) {
                String[] str = allUserName.split(",");
                strings = Arrays.asList(str);
            } else {
                strings.add(allUserName);
            }
        }
        // 1.???????????????????????????  ??????
        Query query = new Query(null, pageNumber, pageData);
        List<UserVO> userVOS = userMapper.getUsersByUserInfo(query, strings);
        List<UserVO> userVOList = new ArrayList<>();
/*        if (userVOS.size() == 0) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.HANDLE_DATA_EXCEPTION.getCode(), "??????????????????");
        }*/
        // 2.???????????????????????????????????????????????????
        // List<String> userNames =new ArrayList<>();
        for (UserVO userVO1 : userVOS) {
            userVO1.activeTimeStr = DateUtil.formatDate(userVO1.activeTime, "yyyy-MM-dd HH:mm:ss");
            // ????????????????????? , ??????????????????????????????,??????????????????,????????????
            List<RoleDO> roleDOS = roleMapper.selectRoleByUserName(userVO1.userName);
            if (roleDOS.size() == 0) {
                userVO1.roles = null;
                userVO1.positions = null;
            } else {
                //????????????
                userVO1.roles = new ArrayList<>();
                //????????????
                userVO1.positions = new ArrayList<>();
                for (RoleDO roleDO : roleDOS) {
                    if(StringUtility.stringEqualsIgnoreCaseObj(UrcConstant.RoleType.position,roleDO.getRoleType())){
                        PositionInfoVO positionInfoVO = new PositionInfoVO();
                        positionInfoVO.positionName= roleDO.getRoleName();
                        userVO1.positions.add(positionInfoVO);
                    }else{
                        RoleVO roleVO = new RoleVO();
                        roleVO.roleName = roleDO.getRoleName();
                        userVO1.roles.add(roleVO);
                    }
                }
            }
            // 4.??????userVo
            userVOList.add(userVO1);
        }
        // ???????????????
        long total = userMapper.getUsersByUserInfoCount(query, strings);
        PageResultVO pageResultVO = new PageResultVO(userVOList, total, pageData);

        return VoHelper.getSuccessResult(pageResultVO);
    }

    /**
     * ??????system
     *
     * @param userName
     * @param sysKey
     * @param ticket
     * @return ResultVO<UserSysVO>
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/12 9:34
     */
    @Override
    public ResultVO<UserSysVO> getSysKeyByUserName(String userName, String sysKey, String ticket) {
        List<String> userRoleDOS = userRoleMapper.getSysKeyByUser(userName);
        try {

            // ??????????????????????????? ??????userName/syskey???context,??????MD5;
            // ??????????????????
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ??????userInfo
     *
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/8 20:43
     */
    public List getUserInfo() {
        List<UserInfo> dingUserList = null;
        // 1.??????token
        JSONObject object = new JSONObject();

        String accessToken = null;
        String userInfo = null;
        Map<String, String> headMap = new HashMap<>();
        StringBuffer sb = new StringBuffer();


        try {
            object.put("username", username);
            object.put("password", password);

            headMap.put("Content-Type", "application/json");

            accessToken = HttpUtility.postHasHeaders(GET_TOKEN, headMap, object.toJSONString(), "utf-8");
            if (StringUtility.isNullOrEmpty(accessToken)) {
                logger.error(String.format("??????token??????, ???????????????: %s ", StringUtility.toJSONString(object)), object);
                throw new URCBizException(CommonMessageCodeEnum.FAIL.getCode(), "??????token ??????,?????????token??????");
            }
            logger.info("??????token");
            // ????????????string ??????json
            JSONObject jsonToken = StringUtility.parseString(accessToken);
            String token = jsonToken.getString("token");
            // 2.?????????UserInfo???????????????UserInfo??????

            sb.append("JWT").append(" ").append(token);

            headMap.put("Authorization", sb.toString());

            userInfo = HttpUtility.getHasHeaders(USER_INFO_ADDRESS, headMap);
            if (StringUtility.isNullOrEmpty(userInfo)) {
                logger.error(String.format("??????userInfo??????, ???????????????: %s token???: %s", USER_INFO_ADDRESS, accessToken), userInfo);
                throw new URCBizException(CommonMessageCodeEnum.FAIL.getCode(), "??????userInfo??????,userInfo??????????????????");
            }
            // ??????json??????
            logger.info(String.format("??????userInfo", userInfo));
            dingUserList = StringUtility.jsonToList(userInfo, UserInfo.class);
        } catch (Exception e) {
            logger.error(String.format("???????????????: %s,%s ,???????????????: %s,?????????token???: %s, ??????????????????: %s", GET_TOKEN, USER_INFO_ADDRESS, StringUtility.toJSONString(object), accessToken, userInfo), e);
        }
        return dingUserList;
    }


    @Override
    public ResultVO login(Map<String, String> map) {
        return login(map.get(StringConstant.userName), map.get(StringConstant.pwd), map.get(StringConstant.ip), map.get(StringConstant.deviceName),
                map.get(StringConstant.deviceType));
    }

    @Autowired
    private IUserLogBp userLogBp;

    public ResultVO login(String userName, String pwd, String ip, String deviceName, String deviceType) {
        try {
            LoginRespVO resp = new LoginRespVO();
            long startTime = System.currentTimeMillis();
            boolean blnOk = ldapBpImpl.validateUser(userName, pwd);
            long endTime = System.currentTimeMillis();

            UserLoginLogDO loginLog = new UserLoginLogDO();
            loginLog.userName = userName;
            loginLog.ip = ip;
            loginLog.ldapCost = endTime - startTime;
            loginLog.loginSuccess = blnOk ? 1 : 0;

            loginLog.loginTime = new Date();
            loginLog.createTime = new Date();
            loginLog.modifiedTime = new Date();
            resp.userName = userName;
            if (blnOk) {
                resp.ticket = userValidateBp.createTicket(userName);
                UserVO getU = cacheBp.getUser(userName, deviceType);
                // ??????????????????
                UserVO u = new UserVO();
                u.userName = userName;
                u.ticket = resp.ticket;
                u.ip = ip;
                u.deviceName = deviceName;
                u.deviceType = deviceType;
                u.loginTime = System.currentTimeMillis();
                cacheBp.insertUser(u);
                //?????????????????????????????????
                backupUserTicketToDB(u);
                //??????????????????
                UserDO userDO = getPersonFromCacheOrDb(u.userName);
                if(userDO == null){
                    resp.personName = u.userName;
                    resp.avatar = "";
                }else{
                    resp.personName = userDO.getChineseName();
                    resp.avatar = userDO.getAvatar();
                }
                loginLog.remark = String.format("????????????:request:[%s,%s,%s],redis ?????????????????????[%s],redis?????????????????????[%s]", userName, pwd, ip, StringUtility.toJSONString(getU), StringUtility.toJSONString(u));
                userLogBp.insertLog(loginLog);
                return VoHelper.getResultVO(ErrorCode.E_000001, "????????????", resp);
            } else {
                return VoHelper.getResultVO(ErrorCode.E_100001, "??????????????????");
            }
        } catch (Exception ex) {
            logger.error(String.format("login ERROR:%s %s %s", userName, pwd, ip), ex);
            throw new URCBizException(ex.getMessage(), ErrorCode.E_000000);
        }
    }

    /**
     * ????????????ticket????????????
     *
     * @param u ????????????
     */
    private void backupUserTicketToDB(UserVO u) {
        Map map = new HashMap(10);
        try {
            //?????????????????????????????????
            UserTicketDO userTicketDO = userTicketMapper.selectUserTicketByUserName(u.userName, u.deviceType);
            map.put("userName", u.userName);
            map.put("ticket", u.ticket);
            map.put("loginIp", u.ip);
            map.put("deviceName", u.deviceName);
            map.put("deviceType", u.deviceType);
            Date now = new Date();
            map.put("modifiedTime", now);
            map.put("loginTime", now);
            //??????????????????
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            //????????????????????????
            calendar.add(Calendar.HOUR, +2);
            map.put("expiredTime", calendar.getTime());
            if (StringUtil.isEmpty(userTicketDO)) {
                //??????????????????????????????
                map.put("createdTime", now);
                userTicketMapper.insertUserTicket(map);
            } else {
                //????????????????????????
                userTicketMapper.updateUserTicket(map);
            }
        } catch (Exception e) {
            logger.error(String.format("Backup user ticket data failed.user:%s", StringUtility.toJSONString(u)), e);
        }

    }

    /**
     * ??????userName??????????????????
     *
     * @param userName
     * @return
     */
    private UserDO getPersonFromCacheOrDb(String userName) {
        if (StringUtility.isNullOrEmpty(userName)) {
            return null;
        }
        String person = cacheBp.getPersonByUserName(userName);
        if (StringUtility.isNullOrEmpty(person)) {
            UserDO byUserName = userMapper.getPersonByUserName(userName);
            if (byUserName == null) {
                return null;
            }
            cacheBp.setPersonByUserName(userName, serializeBp.obj2JsonNonEmpty(byUserName));
            return byUserName;
        }
        return serializeBp.json2ObjNew(person, new TypeReference<UserDO>() {
        });
    }

//    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);

    /**
     * ??????????????????
     *
     * @param loginLog
     * @author panyun@youkeshu.com
     * @date 2018???6???6??? ??????2:20:46
     */
    private void insertLoginLog(UserLoginLogDO loginLog) {
        logger.info(StringUtility.toJSONString_NoException(StringUtility.toJSONString_NoException(loginLog)));

//        fixedThreadPool.execute(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    loginLog.createTime = new Date();
//                    loginLog.modifiedTime = new Date();
//                    userLoginLogMapper.insertUserLoginLog(loginLog);
//                } catch (Exception ex) {
//                    logger.error(StringUtility.toJSONString_NoException(loginLog), ex);
//                }
//            }
//        });
    }

    UserSysVOComparator myUserSysVOComparator = new UserSysVOComparator();

    @Override
    public ResultVO<GetAllFuncPermitRespVO> getAllFuncPermit(String operator, List<String> sysKeys,Integer sysType) {
        // ???????????????
        GetAllFuncPermitRespVO permitCache = cacheBp.getUserFunc(operator, sysKeys,sysType);
        if (permitCache == null || CollectionUtils.isEmpty(permitCache.lstSysRoot)) {
            // ???DB???,???????????????
            permitCache = permitStatBp.updateUserPermitCache(operator);
            if (permitCache.lstUserSysVO != null && permitCache.lstUserSysVO.size() > 0) {
                permitCache.lstSysRoot = new ArrayList<>(permitCache.lstUserSysVO.size());
                Collections.sort(permitCache.lstUserSysVO, myUserSysVOComparator);
                for (UserSysVO us : permitCache.lstUserSysVO) {
                    //????????????
                    if(!StringUtility.stringEqualsIgnoreCaseObj(permissionMapper.getSysType(us.sysKey),sysType)){
                        continue;
                    }
                    permitCache.lstSysRoot.add(us.context);
                }
                permitCache.lstUserSysVO = null;
            }
        }

        if (permitCache != null) {
            permitCache.lstUserSysVO = null;
            if (permitCache.lstSysRoot == null) {
                permitCache.lstSysRoot = new ArrayList<>();
            }
            if (permitCache.funcVersion == null) {
                permitCache.funcVersion = StringUtility.Empty;
            }
            return VoHelper.getSuccessResult(permitCache);
        }
        return VoHelper.getSuccessResult(null);
    }

    @Override
    public ResultVO logout(String jsonStr) {
        JSONObject jo = StringUtility.parseString(jsonStr);
        String strOperator = jo.getString(StringConstant.operator);
        String ticket = jo.getString(StringConstant.ticket);
        String deviceType = jo.getString(StringConstant.deviceType);
        UserVO u = cacheBp.getUser(strOperator, deviceType);
        // ????????????
        UserLoginLogDO logDO = new UserLoginLogDO();
        logDO.userName = strOperator;
        logDO.remark = String.format("????????????:request :[%s]redis??????[%S]", jsonStr, StringUtility.toJSONString(u));
        logDO.loginTime = new Date();
        logDO.createTime = new Date();
        logDO.modifiedTime = new Date();
        this.insertLoginLog(logDO);
        //????????????????????????ticket??????
        userTicketMapper.deleteUserTicketByUserName(strOperator, deviceType);
        if (u == null || !StringUtils.equalsIgnoreCase(u.ticket, ticket)) {
            throw new URCBizException(ErrorCode.E_100002);
        }
        cacheBp.removeUser(strOperator, deviceType);
        return VoHelper.getSuccessResult("logout success");
    }

    @Override
    public List<String> getUserName(String userName) {
        return userMapper.getUserNameByFuzzy(userName);
    }

}
