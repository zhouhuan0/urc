package com.yks.urc.mapper;

import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.UserDO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 〈一句话功能简述〉
 * userMapper单元测试类
 *
 * @author lvcr
 * @version 1.0
 * @date 2018/6/7 16:12
 * @see UserMapperTest
 * @since JDK1.8
 */
public class UserMapperTest extends BaseMapperTest {

    @Autowired
    private IUserMapper userMapper;

    @Test
    public void testInsert() {
        List<UserDO> users = userMapper.listUsersByRoleId(1);
        Assert.assertNotNull(users);
    }



}
