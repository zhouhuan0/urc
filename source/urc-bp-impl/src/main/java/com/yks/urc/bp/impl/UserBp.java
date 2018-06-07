package com.yks.urc.bp.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.yks.urc.fw.HttpUtility;
import com.yks.urc.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserBp {
    private static Logger logger = LoggerFactory.getLogger(UserBp.class);
    /**
     * token 请求地址
     */
    private static final String GET_TOKEN = "https://userinfo.youkeshu.com/api/get_token";
    /**
     * 获取钉钉信息地址
     */
    private static final String DING_DING = "https://userinfo.youkeshu.com/api/UserList/";
    private static String username = "linwanxian";
    private static String password = "linwx123";

    /**
     * 同步数据
     */
    public void SynUserFromUserInfo() {
        //1.请求token
        JSONObject object = new JSONObject();
        object.put("username", username);
        object.put("password", password);
        try {
            String accessToken = HttpUtility.sendPost(GET_TOKEN, object.toJSONString());
            System.out.println(accessToken);
            //2.只调用UserInfo接口，同步UserInfo数据
            UserVO userVO = new UserVO();
           // String token =  JSONPath.contains(accessToken,"$.token");
           // String userInfo = HttpUtility.sendGet(DING_DING,token);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        UserBp userBp = new UserBp();
        userBp.SynUserFromUserInfo();
    }
}
