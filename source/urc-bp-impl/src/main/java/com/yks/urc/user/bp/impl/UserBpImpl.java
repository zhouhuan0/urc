package com.yks.urc.user.bp.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.common.util.DateUtil;
import com.yks.distributed.lock.core.DistributedReentrantLock;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.comparator.impl.UserSysVOComparator;
import com.yks.urc.entity.UserDO;
import com.yks.urc.entity.UserInfo;
import com.yks.urc.entity.UserLoginLogDO;
import com.yks.urc.entity.UserPermissionCacheDO;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.exception.URCServiceException;
import com.yks.urc.fw.HttpUtility;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.ldap.bp.api.ILdapBp;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IUserLoginLogMapper;
import com.yks.urc.mapper.IUserMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.user.bp.api.IUserBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.Query;
import com.yks.urc.vo.helper.VoHelper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    /**
     * 同步UserInfo数据
     *
     * @Author: linwanxian@youkeshu.com
     * @Date: 2018/6/8 15:29
     */
    DistributedReentrantLock lock = new DistributedReentrantLock("SynUserFromUserInfo");

    @Override
    public ResultVO SynUserFromUserInfo(String username) {
        if (lock.tryLock()) {
            try {
                List<UserInfo> userInfoList = this.getUserInfo();
                if (userInfoList == null || userInfoList.size() == 0) {
                    logger.error(String.format("请求userInfo 接口异常"));
                    operationBp.addLog(this.getClass().getName(), "请求userInfo 接口异常", null);
                    throw new URCBizException("请求userInfo 接口异常", ErrorCode.E_000000);
                }
                List<UserDO> userDoList = new ArrayList<>();

                // 先清理数据表
                userMapper.deleteUrcUser();
                logger.info("清理完成,开始同步");
                for (UserInfo user : userInfoList) {
                    UserDO userDo = new UserDO();
                    userDo.setUserName(user.username);
                    userDo.setDingUserId(user.ding_userid);
                    userDo.setCreateBy(username);
                    userDo.setModifiedBy(username);
                    userDo.setCreateTime(StringUtility.getDateTimeNow());
                    userDo.setModifiedTime(StringUtility.getDateTimeNow());
                    userDo.setActiveTime(StringUtility.stringToDate(user.date_joined, "yyyy-MM-dd HH:mm:ss"));
                    // 1 表示启用,0表示禁用
                    if ("66050".equals(user.userAccountControl)) {
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
            } catch (Exception e) {
                throw  new URCServiceException(CommonMessageCodeEnum.FAIL.getCode(), "同步userInfo数据出错..",e);
            } finally {
                lock.unlock();
            }
        } else {
            logger.info("同步userInfo数据正在执行...,");
            if (!"system".equals(username)) {
                // 手动触发正在执行..记录日志
                operationBp.addLog(this.getClass().getName(), "手动触发正在执行..", null);
                return VoHelper.getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), "手动触发正在执行..");
            } else {
                // 定时任务触发正在执行..记录日志
                operationBp.addLog(this.getClass().getName(), "定时任务正在执行..", null);
                return VoHelper.getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), "定时任务正在执行..");
            }
        }

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
            // 查询角色 , 有的用户可能没有角色,则不装载角色,角色为空
            List<String> roleNameList = roleMapper.selectRoleNameByUserName(userVO1.userName);
            if (roleNameList.size() == 0) {
              userVO1.roles =null;
            }else {
                //组装角色
                userVO1.roles =new ArrayList<>();
                for (String roleName : roleNameList) {
                    RoleVO roleVO = new RoleVO();
                    roleVO.roleName = roleName;
                    userVO1.roles.add(roleVO);
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
        String accessToken =null;
        String userInfo = null;
        try {
            accessToken=  HttpUtility.sendPost(GET_TOKEN, object.toJSONString());
            if (StringUtility.isNullOrEmpty(accessToken)) {
                logger.error(String.format("获取token失败, 请求参数为: %s ",StringUtility.toJSONString(object)),object);
               throw new URCBizException(CommonMessageCodeEnum.FAIL.getCode(),"获取token 失败,返回的token为空");
            }
            logger.info("获取token");
            // 将拿到的string 转为json
            JSONObject jsonToken = StringUtility.parseString(accessToken);
            String token = jsonToken.getString("token");
            // 2.只调用UserInfo接口，同步UserInfo数据
             userInfo = HttpUtility.httpGet(USER_INFO_ADDRESS + token);
            if (StringUtility.isNullOrEmpty(userInfo)) {
                logger.error(String.format("获取userInfo失败, 请求参数为: %s token为: %s",USER_INFO_ADDRESS ,accessToken),userInfo);
                throw new URCBizException(CommonMessageCodeEnum.FAIL.getCode(),"获取userInfo失败,userInfo返回数据为空");
            }
            // 解析json数组
            logger.info(String.format("获取userInfo",userInfo));
            dingUserList = StringUtility.jsonToList(userInfo, UserInfo.class);
        } catch (Exception e) {
            logger.error(String.format("请求地址为: %s,%s ,请求参数为: %s,获取的token为: %s, 调用的结果为: %s",GET_TOKEN,USER_INFO_ADDRESS,StringUtility.toJSONString(object),accessToken,userInfo),e);
        }
        return dingUserList;
    }


    @Override
    public ResultVO login(Map<String, String> map) {
        return login(map.get(StringConstant.userName), map.get(StringConstant.pwd), map.get(StringConstant.ip));
    }

    public ResultVO login(String userName,String pwd,String ip) {
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
            loginLog.remark = String.format("PWD:%s", pwd);
            loginLog.loginTime = new Date();
            this.insertLoginLog(loginLog);
            resp.userName = userName;
            if (blnOk) {
                resp.ticket = userValidateBp.createTicket(userName, ip);
                // 缓存用户信息
                UserVO u = new UserVO();
                u.userName = userName;
                u.ticket = resp.ticket;
                u.ip = ip;
                cacheBp.insertUser(u);
                resp.personName = getPersonNameFromCacheOrDb(u.userName);// userMapper.getPersonNameByUserName(u.userName);
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
     * 根据userName获取中文姓名
     * @param userName
     * @return
     */
    private String getPersonNameFromCacheOrDb(String userName) {
        if (StringUtility.isNullOrEmpty(userName)) return userName;
        String personName = cacheBp.getPersonNameByUserName(userName);
        if (StringUtility.isNullOrEmpty(personName)) {
            personName = userMapper.getPersonNameByUserName(userName);
            if (StringUtility.isNullOrEmpty(personName)) {
                personName = userName;
            }
            cacheBp.setPersonNameByUserName(userName, personName);
        }
        return personName;
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
    public ResultVO<GetAllFuncPermitRespVO> getAllFuncPermit(String operator) {
        // 先从缓存取
        GetAllFuncPermitRespVO permitCache = cacheBp.getUserFunc(operator);
        if (permitCache == null) {
            // 从DB取,并更新缓存
            permitCache = permitStatBp.updateUserPermitCache(operator);
            if (permitCache.lstUserSysVO != null && permitCache.lstUserSysVO.size() > 0) {
                permitCache.lstSysRoot = new ArrayList<>(permitCache.lstUserSysVO.size());
                Collections.sort(permitCache.lstUserSysVO, myUserSysVOComparator);
                for (UserSysVO us : permitCache.lstUserSysVO) {
                    permitCache.lstSysRoot.add(us.context);
                }
                permitCache.lstUserSysVO = null;
            }
        }
        if (permitCache != null) {
            permitCache.lstUserSysVO = null;
            if (permitCache.lstSysRoot == null) permitCache.lstSysRoot = new ArrayList<>();
            if (permitCache.funcVersion == null) permitCache.funcVersion = StringUtility.Empty;
            return VoHelper.getSuccessResult(permitCache);
        }
        return VoHelper.getSuccessResult(null);
    }

    @Override
    public ResultVO logout(String jsonStr) {
        JSONObject jo = StringUtility.parseString(jsonStr);
        String strOperator = jo.getString(StringConstant.operator);
        String ticket = jo.getString(StringConstant.ticket);
        UserVO u = cacheBp.getUser(strOperator);
        if (u == null || !StringUtils.equalsIgnoreCase(u.ticket, ticket)) {
            throw new URCBizException(ErrorCode.E_100002);
        }
        cacheBp.removeUser(strOperator);
        return VoHelper.getSuccessResult("logout success");
    }

    public static void main(String[] args) {
        List list = new ArrayList();
        list.add("linwanxian");
        list.add("panyun");
        String str = list.toString();
        System.out.println("======================" + str);
    }
}
