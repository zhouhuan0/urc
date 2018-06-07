package com.yks.urc.mapper;

import com.yks.urc.entity.UserLoginLogDO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
 /**
  * 〈一句话功能简述〉 
  *  用户登录日志mapper单元测试类
  * @author lvcr
  * @version 1.0 
  * @see UserLoginLogMapperTest 
  * @since JDK1.8
  * @date 2018/6/7 16:12
  */ 
public class UserLoginLogMapperTest extends BaseMapperTest{

    @Autowired
    private IUserLoginLogMapper userLoginLogMapper;

    @Test
    public void testInsert(){
        UserLoginLogDO userLoginLogDO = new UserLoginLogDO();
        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format( new Date());
        Timestamp time = Timestamp.valueOf(current);
        userLoginLogDO.loginTime=time;
        userLoginLogDO.userName="admin12122";
        int rtn = userLoginLogMapper.insertUserLoginLog(userLoginLogDO);
        Assert.assertEquals(1,rtn);
    }

    @Test
    public void testSelect(){
        UserLoginLogDO userLoginLogDO = userLoginLogMapper.selectUserLoginLog();
        String dateStr  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(userLoginLogDO.loginTime);
        System.out.println(dateStr);
    }
}
