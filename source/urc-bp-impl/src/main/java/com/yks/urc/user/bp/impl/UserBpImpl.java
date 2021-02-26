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
     * token 请求地址
     */
    @Value("${userInfo.token}")
    private String GET_TOKEN;
    /**
     * 获取UserInfo信息地址
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
     * 同步UserInfo数据 lock name
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2020-08-06 14:23
     */
    private String lkName = "SynUserFromUserInfo";

    @Override
    public ResultVO SynUserFromUserInfo(String username) {
        if (!lockBp.tryLock(lkName)) {
            logger.info("同步userInfo数据正在执行...,");
            // 定时任务触发正在执行..记录日志
            operationBp.addLog(this.getClass().getName(), "正在执行..", null);
            return VoHelper.getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), "正在执行..");
        }

        try {
           /* List<UserInfo> userInfoList = this.getUserInfo();
            if (userInfoList == null || userInfoList.size() == 0) {
                logger.error(String.format("请求userInfo 接口异常"));
                operationBp.addLog(this.getClass().getName(), "请求userInfo 接口异常", null);
                throw new URCBizException("请求userInfo 接口异常", ErrorCode.E_000000);
            }*/
            return BeanProvider.getBean(IUserBp.class).updateAllUserInfoNew();
        } catch (Exception e) {
            throw new URCServiceException(CommonMessageCodeEnum.FAIL.getCode(), "同步userInfo数据出错..", e);
        } finally {
            lockBp.unlock(lkName);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVO updateAllUserInfo(List<UserInfo> userInfoList) throws Exception {
        if (CollectionUtils.isEmpty(userInfoList)) {
            return VoHelper.getFail("入参为空");
        }
        List<UserDO> userDoList = new ArrayList<>();

        // 先清理数据表
        userMapper.deleteUrcUser();
        logger.info("清理完成,开始同步");
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
            // 1 表示启用,0表示禁用
            if ("66050".equals(user.ad_control_number)) {
                userDo.setIsActive(0);
            } else {
                userDo.setIsActive(1);
            }
            // 传入手动同步的创建人员
            userDo.setCreateBy(username);
            userDo.setModifiedBy(username);
            userDoList.add(userDo);
            //如果超过1000条, 先插入数据库,然后将list清空,在进行装载
            if (userDoList.size() >= 1000) {
                userMapper.insertBatchUser(userDoList);
                userDoList.clear();
            }
        }
        //如果跑完了,发现仍然还有数据,再插入一次
        if (userDoList.size() != 0) {
            userMapper.insertBatchUser(userDoList);
        }
        operationBp.addLog(this.getClass().getName(), "同步userInfo数据成功..", null);
        return VoHelper.getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), "同步userInfo数据成功..");
    }

    @Override
    public ResultVO getAllFuncPermitForOtherSystem(String operator, List<String> sysKeys) {
        // 先从缓存取
        GetAllFuncPermitRespVO permitCache = cacheBp.getUserFunc(operator, sysKeys,UrcConstant.SysType.FBA);
        if (permitCache == null) {
            // 从DB取,并更新缓存
            permitCache = permitStatBp.updateUserPermitCache(operator);
            if (permitCache.lstUserSysVO != null && permitCache.lstUserSysVO.size() > 0) {
                permitCache.lstSysRoot = new ArrayList<>(permitCache.lstUserSysVO.size());
                Collections.sort(permitCache.lstUserSysVO, myUserSysVOComparator);
                for (UserSysVO us : permitCache.lstUserSysVO) {
                    //排除系统
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
    public ResultVO updateAllUserInfoNew() {
        List<UserDO> userDoList = new ArrayList<>();
        logger.info("开始同步");
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
            return VoHelper.getFail("同步失败,请稍后重试");
        }
        JSONObject jsonObject = JSON.parseObject(responseString);
        if(!StringUtility.stringEqualsIgnoreCase(CommonMessageCodeEnum.SUCCESS.getCode(),jsonObject.getString("state"))){
            return VoHelper.getFail(jsonObject.getString("msg"));
        }
        JSONObject objectJSONObject = jsonObject.getJSONObject("data");
        JSONArray list = objectJSONObject.getJSONArray("list");
        //没有可更新的数据直接返回成功
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
            userDo.setCreateBy(username);
            userDo.setModifiedBy(username);
            userDo.setCreateTime(StringUtility.getDateTimeNow());
            userDo.setModifiedTime(StringUtility.getDateTimeNow());
            userDo.setActiveTime(StringUtility.isNullOrEmpty(activeTime) || activeTime.length()< 13 ? new Date() : new Date(Long.valueOf(activeTime)));
            userDo.setAvatar(avatar);
            // 1在职，2离职
            if (StringUtility.stringEqualsIgnoreCase(isActive,"2")) {
                userDo.setIsActive(0);
            } else {
                userDo.setIsActive(1);
            }
            // 传入手动同步的创建人员
            userDo.setCreateBy(username);
            userDo.setModifiedBy(username);
            userDoList.add(userDo);
            //如果超过1000条, 先插入数据库,然后将list清空,在进行装载
            if (userDoList.size() >= 1000) {
                userMapper.insertOrUpdateBatchUser(userDoList);
                userDoList.clear();
            }
        }
        //如果跑完了,发现仍然还有数据,再插入一次
        if (userDoList.size() != 0) {
            userMapper.insertOrUpdateBatchUser(userDoList);
        }
        operationBp.addLog(this.getClass().getName(), "同步userInfo数据成功..", null);
        configBp.update2Db("synUserInfoPoint", DateUtil.formatDate(dateTimeNow, DateUtil.YYYY_MM_DD_HH_MM_SS));
        return VoHelper.getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), "同步userInfo数据成功..");
    }

    /**
     * 搜索用户 , 首先拿到和用户 相关的所有信息, 再根据用户名去查询
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
        //先切割UserVo 的username
        List<String> strings = new ArrayList<>();
        if (userVO != null) {
            String allUserName = userVO.userName;
            //根据 , 切割用户名,用数组装,转成list
            if (allUserName.contains(",")) {
                String[] str = allUserName.split(",");
                strings = Arrays.asList(str);
            } else {
                strings.add(allUserName);
            }
        }
        // 1.首先查询出所有数据  分页
        Query query = new Query(null, pageNumber, pageData);
        List<UserVO> userVOS = userMapper.getUsersByUserInfo(query, strings);
        List<UserVO> userVOList = new ArrayList<>();
/*        if (userVOS.size() == 0) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.HANDLE_DATA_EXCEPTION.getCode(), "查询结果为空");
        }*/
        // 2.将拿到的用户名再分别去获取角色名称
        // List<String> userNames =new ArrayList<>();
        for (UserVO userVO1 : userVOS) {
            userVO1.activeTimeStr = DateUtil.formatDate(userVO1.activeTime, "yyyy-MM-dd HH:mm:ss");
            // 查询角色或岗位 , 有的用户可能没有角色,则不装载角色,角色为空
            List<RoleDO> roleDOS = roleMapper.selectRoleByUserName(userVO1.userName);
            if (roleDOS.size() == 0) {
                userVO1.roles = null;
                userVO1.positions = null;
            } else {
                //组装角色
                userVO1.roles = new ArrayList<>();
                //组装岗位
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
            // 4.组装userVo
            userVOList.add(userVO1);
        }
        // 获取总条数
        long total = userMapper.getUsersByUserInfoCount(query, strings);
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
    @Override
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
                logger.error(String.format("获取token失败, 请求参数为: %s ", StringUtility.toJSONString(object)), object);
                throw new URCBizException(CommonMessageCodeEnum.FAIL.getCode(), "获取token 失败,返回的token为空");
            }
            logger.info("获取token");
            // 将拿到的string 转为json
            JSONObject jsonToken = StringUtility.parseString(accessToken);
            String token = jsonToken.getString("token");
            // 2.只调用UserInfo接口，同步UserInfo数据

            sb.append("JWT").append(" ").append(token);

            headMap.put("Authorization", sb.toString());

            userInfo = HttpUtility.getHasHeaders(USER_INFO_ADDRESS, headMap);
            if (StringUtility.isNullOrEmpty(userInfo)) {
                logger.error(String.format("获取userInfo失败, 请求参数为: %s token为: %s", USER_INFO_ADDRESS, accessToken), userInfo);
                throw new URCBizException(CommonMessageCodeEnum.FAIL.getCode(), "获取userInfo失败,userInfo返回数据为空");
            }
            // 解析json数组
            logger.info(String.format("获取userInfo", userInfo));
            dingUserList = StringUtility.jsonToList(userInfo, UserInfo.class);
        } catch (Exception e) {
            logger.error(String.format("请求地址为: %s,%s ,请求参数为: %s,获取的token为: %s, 调用的结果为: %s", GET_TOKEN, USER_INFO_ADDRESS, StringUtility.toJSONString(object), accessToken, userInfo), e);
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
                // 缓存用户信息
                UserVO u = new UserVO();
                u.userName = userName;
                u.ticket = resp.ticket;
                u.ip = ip;
                u.deviceName = deviceName;
                u.deviceType = deviceType;
                u.loginTime = System.currentTimeMillis();
                cacheBp.insertUser(u);
                //缓存的同时备份到数据库
                backupUserTicketToDB(u);
                //获取用户信息
                UserDO userDO = getPersonFromCacheOrDb(u.userName);
                if(userDO == null){
                    resp.personName = u.userName;
                    resp.avatar = "";
                }else{
                    resp.personName = userDO.getChineseName();
                    resp.avatar = userDO.getAvatar();
                }
                loginLog.remark = String.format("登陆操作:request:[%s,%s,%s],redis 原有的用户信息[%s],redis新增的用户信息[%s]", userName, pwd, ip, StringUtility.toJSONString(getU), StringUtility.toJSONString(u));
                userLogBp.insertLog(loginLog);
                return VoHelper.getResultVO(ErrorCode.E_000001, "登陆成功", resp);
            } else {
                return VoHelper.getResultVO(ErrorCode.E_100001, "账号密码错误");
            }
        } catch (Exception ex) {
            logger.error(String.format("login ERROR:%s %s %s", userName, pwd, ip), ex);
            throw new URCBizException(ex.getMessage(), ErrorCode.E_000000);
        }
    }

    /**
     * 备份用户ticket到数据库
     *
     * @param u 用户信息
     */
    private void backupUserTicketToDB(UserVO u) {
        Map map = new HashMap(10);
        try {
            //先查询用户记录是否存在
            UserTicketDO userTicketDO = userTicketMapper.selectUserTicketByUserName(u.userName, u.deviceType);
            map.put("userName", u.userName);
            map.put("ticket", u.ticket);
            map.put("loginIp", u.ip);
            map.put("deviceName", u.deviceName);
            map.put("deviceType", u.deviceType);
            Date now = new Date();
            map.put("modifiedTime", now);
            map.put("loginTime", now);
            //获取当前时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            //两个小时之后过期
            calendar.add(Calendar.HOUR, +2);
            map.put("expiredTime", calendar.getTime());
            if (StringUtil.isEmpty(userTicketDO)) {
                //插入一条用户登录记录
                map.put("createdTime", now);
                userTicketMapper.insertUserTicket(map);
            } else {
                //更新用户登录信息
                userTicketMapper.updateUserTicket(map);
            }
        } catch (Exception e) {
            logger.error(String.format("Backup user ticket data failed.user:%s", StringUtility.toJSONString(u)), e);
        }

    }

    /**
     * 根据userName获取中文姓名
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
     * 登录日志入库
     *
     * @param loginLog
     * @author panyun@youkeshu.com
     * @date 2018年6月6日 下午2:20:46
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
        // 先从缓存取
        GetAllFuncPermitRespVO permitCache = cacheBp.getUserFunc(operator, sysKeys,sysType);
        if (permitCache == null || CollectionUtils.isEmpty(permitCache.lstSysRoot)) {
            // 从DB取,并更新缓存
            permitCache = permitStatBp.updateUserPermitCache(operator);
            if (permitCache.lstUserSysVO != null && permitCache.lstUserSysVO.size() > 0) {
                permitCache.lstSysRoot = new ArrayList<>(permitCache.lstUserSysVO.size());
                Collections.sort(permitCache.lstUserSysVO, myUserSysVOComparator);
                for (UserSysVO us : permitCache.lstUserSysVO) {
                    //排除系统
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
        // 记录登出
        UserLoginLogDO logDO = new UserLoginLogDO();
        logDO.userName = strOperator;
        logDO.remark = String.format("登出操作:request :[%s]redis信息[%S]", jsonStr, StringUtility.toJSONString(u));
        logDO.loginTime = new Date();
        logDO.createTime = new Date();
        logDO.modifiedTime = new Date();
        this.insertLoginLog(logDO);
        //删除数据库的用户ticket信息
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
