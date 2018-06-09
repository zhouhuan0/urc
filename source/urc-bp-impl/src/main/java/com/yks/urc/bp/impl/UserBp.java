package com.yks.urc.bp.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.entity.UserInfo;
import com.yks.urc.entity.UrcUserDo;
import com.yks.urc.fw.HttpUtility;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IUrcUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class UserBp {
    private static Logger logger = LoggerFactory.getLogger(UserBp.class);
    /**
     * token 请求地址
     */
    @Value("${userInfo.token}")
    private static String GET_TOKEN ;
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
    private IUrcUserMapper userMapper;

    /**
     * 同步UserInfo数据
     *
     * @Author: linwanxian@youkeshu.com
     * @Date: 2018/6/8 15:29
     */
    @Transactional(rollbackFor = Exception.class)
    public void SynUserFromUserInfo(String username) {
        List<UserInfo> userInfoList = this.getUserInfo();
        UrcUserDo userDo = new UrcUserDo();
        for (UserInfo user : userInfoList) {
            userDo.setUsername(user.username);
            userDo.setDingUserId(user.ding_userid);
            userDo.setCreateBy(username);
            userDo.setModifiedBy(username);
            try {
                userDo.setActiveTime(StringUtility.stringToDate(user.date_joined,"yyyy-MM-dd HH:mm:ss"));
                //0 表示启用,1表示禁用
                if ("66050".equals(user.userAccountControl)) {
                    userDo.setIsActive(1);
                } else if ("66048".equals(user.userAccountControl) || "512".equals(user.userAccountControl)) {
                    userDo.setIsActive(0);
                } else {
                    userDo.setIsActive(Integer.parseInt(user.userAccountControl));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //传入手动同步的创建人员
        userDo.setCreateBy(username);
        userDo.setModifiedBy(username);
        List<UrcUserDo> userDoList = new ArrayList<>();
        userDoList.add(userDo);
        //先清理数据表
        userMapper.deleteUrcUser();
        logger.info("清理完成,开始同步");
        userMapper.insertBatchUser(userDoList);
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
