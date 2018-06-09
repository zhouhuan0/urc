package com.yks.urc.mapper;

import com.yks.urc.entity.RoleDO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    /**
     * 根据roleName做唯一约束，roleName不重复则添加，roleName重复则更新
     * rtn=2表示更新
     * rtn=1表示添加
     */
    @Test
    public void insertOrUpdate() {
        RoleDO roleDO = new RoleDO();
        roleDO.setRoleName("admin121");
        roleDO.setActive(Boolean.TRUE);
        roleDO.setAuthorizable(Boolean.TRUE);
        roleDO.setCreateBy("admin");
        roleDO.setEffectiveTime(new Date());
        roleDO.setCreateTime(new Date());
        roleDO.setForever(Boolean.FALSE);
        int rtn = roleMapper.insertOrUpdate(roleDO);
        System.out.println(rtn);
    }

    @Test
    public void deleteBatch() {
        List<Integer> ids = new ArrayList();
        ids.add(9);
        ids.add(15);
        int rtn = roleMapper.deleteBatch(ids);
        System.out.println(rtn);

    }

    @Test
    public void getRoleByRoleId() {
        RoleDO roleDO = roleMapper.getRoleByRoleId(1);
        Assert.assertNotNull(roleDO);
    }


}
