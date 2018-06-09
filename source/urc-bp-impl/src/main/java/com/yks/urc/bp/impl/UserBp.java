package com.yks.urc.bp.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.entity.DingDingUser;
import com.yks.urc.entity.UrcUserDo;
import com.yks.urc.fw.HttpUtility;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IUrcUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserBp {
    private static Logger logger = LoggerFactory.getLogger(UserBp.class);
    /**
     * token 请求地址
     */
    @Value("${getToken}")
    private static String GET_TOKEN = "https://userinfo.youkeshu.com/api/get_token";
    /**
     * 获取钉钉信息地址
     */
    @Value("${getDingDingInfo}")
    private static String DING_DING = "https://userinfo.youkeshu.com/api/UserList/?token=";
    private static String username = "linwanxian";
    private static String password = "linwx123";

    @Autowired
    private IUrcUserMapper userMapper;

    /**
     * 同步数据
     *
     * @Author: linwanxian@youkeshu.com
     * @Date: 2018/6/8 15:29
     */
    public void SynUserFromUserInfo(String username) {
        List<DingDingUser> dingUserList = this.getUserInfo();
        UrcUserDo userDo = new UrcUserDo();
        userDo.setUsername(dingUserList.get(0).username);
        userDo.setDingUserId(dingUserList.get(1).ding_userid);
        userDo.setIsActive(Integer.parseInt(dingUserList.get(2).userAccountControl));
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
        List<DingDingUser> dingUserList = null;
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
            String userInfo = HttpUtility.httpGet(DING_DING + token);
            //解析json数组
            logger.info("获取userInfo");
            dingUserList = StringUtility.jsonToList(userInfo, DingDingUser.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return dingUserList;
    }
}
