package com.yks.urc.mapper;

import com.yks.urc.entity.RoleDO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 〈一句话功能简述〉
 * 用户角色管理mapper单元测试类
 *
 * @author lvcr
 * @version 1.0
 * @date 2018/6/7 16:12
 * @see UserRoleMapperTest
 * @since JDK1.8
 */
public class UserRoleMapperTest extends BaseMapperTest {

    @Autowired
    private IRoleMapper roleMapper;

    @Test
    public void testInsert() {
        RoleDO roleDO = new RoleDO();
        roleDO.setActive(Boolean.TRUE);
        roleDO.setAuthorizable(Boolean.TRUE);
        roleDO.setCreateBy("admin");
        roleDO.setEffectiveTime(new Date());
        roleDO.setCreateTime(new Date());
        roleDO.setForever(Boolean.FALSE);
        roleDO.setRoleName("admin");
        int rtn = roleMapper.insert(roleDO);
        Assert.assertEquals(1, rtn);
    }



}
