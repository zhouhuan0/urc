package com.yks.urc.mapper;

import com.yks.urc.TestMapperApplication;
import com.yks.urc.entity.RoleDO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestMapperApplication.class)
public class RoleMapperTest {

    @Autowired
    private RoleMapper roleMapper;

    @Test
    public void testInsert() {
        RoleDO roleDO = new RoleDO();
        roleDO.setRoleId(UUID.randomUUID().toString().substring(0, 32));
        roleDO.setActive(Boolean.TRUE);
        roleDO.setAuthorizable(Boolean.TRUE);
        roleDO.setCreatedBy("admin");
        roleDO.setEffectiveTime(new Date());
        roleDO.setCreatedTime(new Date());
        roleDO.setForever(Boolean.FALSE);
        roleDO.setRoleName("admin");
        int rtn = roleMapper.insert(roleDO);
        Assert.assertEquals(1, rtn);
    }

}
