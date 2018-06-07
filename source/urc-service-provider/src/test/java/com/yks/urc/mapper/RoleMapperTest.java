package com.yks.urc.mapper;

import com.yks.urc.entity.RoleDO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.UUID;

/**
 * 〈一句话功能简述〉
 * 角色管理mapper单元测试类
 *
 * @author lvcr
 * @version 1.0
 * @date 2018/6/7 16:12
 * @see RoleMapperTest
 * @since JDK1.8
 */
public class RoleMapperTest extends BaseMapperTest {

    @Autowired
    private IRoleMapper roleMapper;

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
