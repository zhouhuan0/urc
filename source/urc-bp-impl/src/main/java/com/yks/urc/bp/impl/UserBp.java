package com.yks.urc.bp.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.entity.UserDO;
import com.yks.urc.entity.UserInfo;
import com.yks.urc.entity.UserRoleDO;
import com.yks.urc.fw.HttpUtility;
import com.yks.urc.fw.MD5Utils;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IUserMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserBp {
    private static Logger logger = LoggerFactory.getLogger(UserBp.class);
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

    /**
     * 同步UserInfo数据
     *
     * @Author: linwanxian@youkeshu.com
     * @Date: 2018/6/8 15:29
     */
	//DistributedReentrantLock lock = new DistributedReentrantLock("SynUserFromUserInfo");
    @Transactional(rollbackFor = Exception.class)
    public void SynUserFromUserInfo(String username) {
    	//if(lock.tryLock()){
    		List<UserInfo> userInfoList = this.getUserInfo();
    		UserDO userDo = new UserDO();
    		for (UserInfo user : userInfoList) {
    			userDo.setUserName(user.username);
    			userDo.setDingUserId(user.ding_userid);
    			userDo.setCreateBy(username);
    			userDo.setModifiedBy(username);
    			try {
    				userDo.setActiveTime(StringUtility.stringToDate(user.date_joined, "yyyy-MM-dd HH:mm:ss"));
    				//1 表示启用,0表示禁用
    				if ("66050".equals(user.userAccountControl)) {
    					userDo.setIsActive(0);
    				} else if ("66048".equals(user.userAccountControl) || "512".equals(user.userAccountControl)) {
    					userDo.setIsActive(1);
    				} else {
    					userDo.setIsActive(Integer.parseInt(user.userAccountControl));
    				}
    				//传入手动同步的创建人员
    				userDo.setCreateBy(username);
    				userDo.setModifiedBy(username);
    				List<UserDO> userDoList = new ArrayList<>();
    				userDoList.add(userDo);
    				//先清理数据表
    				userMapper.deleteUrcUser();
    				logger.info("清理完成,开始同步");
    				userMapper.insertBatchUser(userDoList);
    			} catch (Exception e) {
    				e.printStackTrace();
    			//}finally{
    			//	lock.unlock();
    			//}
    		}
    	//}else{
	       // logger.info("同步userInfo数据正在执行...,");
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
        //1.首先查询出所有数据,将userDo的数据组装到uservo
        PageResultVO pageResultVO = new PageResultVO();
        List<UserVO> userVOList = new ArrayList<>();
        List<UserDO> userDOList = userMapper.getUsersByUserInfo(userVO, pageNumber, pageData);
        for (UserDO userDO : userDOList) {
            userVO.userName = userDO.getUserName();
            userVO.personName = userDO.getPerson().getPersonName();
            userVO.activeTime = userDO.getActiveTime().toString();
            //1 为启用
            userVO.isActive = userDO.getIsActive() == 1 ? true : false;
        }

        //2.将拿到的用户名再去获取角色名称
        RoleVO roleVO = new RoleVO();
        for (UserDO user : userDOList) {
            String username = roleMapper.selectRoleName(user.getUserName());
            roleVO.roleName = username;
        }

        //3.将拿到的角色对象组装到uservo里面
        List<RoleVO> list = new ArrayList();
        list.add(roleVO);
        userVO.roles = list;

        //4.组装userVo
        userVOList.add(userVO);

        //5.在把所有的数据组装到pageresult中
        pageResultVO.lst = userVOList;
        //获取总条数
        pageResultVO.total = userMapper.getUsersByUserInfoCount(userVO);
        ResultVO resultVO = new ResultVO();

        //将pagevo组装到resultvo中
        resultVO.data = pageResultVO;
        return resultVO;
    }

    /** 获取system
     * @param userName
     * @param sysKey
     * @param ticket
     * @return ResultVO<UserSysVO>
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/12 9:34
     */
    public ResultVO<UserSysVO> getSysKeyByUserName(String userName, String sysKey, String ticket) {
        List<UserRoleDO> userRoleDOS =userRoleMapper.getSysKeyByUser(userName);
        try {

            //功能版本的生成逻辑 根据userName/syskey取context,进行MD5;
            //获取功能权限
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
        //1.请求token
        JSONObject object = new JSONObject();
        object.put("username", username);
        object.put("password", password);
        try {
            String accessToken = HttpUtility.sendPost(GET_TOKEN, object.toJSONString());
            logger.info("获取token");
            //将拿到的string 转为json
            JSONObject jsonToken = StringUtility.parseString(accessToken);
            String token = jsonToken.getString("token");
            //2.只调用UserInfo接口，同步UserInfo数据
            String userInfo = HttpUtility.httpGet(USER_INFO_ADDRESS + token);
            //解析json数组
            logger.info("获取userInfo");
            dingUserList = StringUtility.jsonToList(userInfo, UserInfo.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return dingUserList;
    }

    public static void main(String[] args) {
        UserBp userBp = new UserBp();
        userBp.SynUserFromUserInfo("lwx");
    }

}
