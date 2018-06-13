package com.yks.urc.service;

import com.yks.urc.entity.RoleDO;
import com.yks.urc.service.api.IRoleService;
import com.yks.urc.vo.RoleVO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoleServiceTest extends BaseServiceTest {

    @Autowired
    private IRoleService roleService;


    @Test
    public void testDeleteRoles() {
        List<Integer> ids = new ArrayList();
        ids.add(1);
        ids.add(2);
        ids.add(3);
        roleService.deleteRoles(ids);
    }

    @Test
    public void getRoleByRoleId() {
        RoleDO roleDO = roleService.getRoleByRoleId("1");
        Assert.assertNull(roleDO);
    }

    @Test
    public void insertOrUpdate(){
        String userName="admin";
        RoleVO roleVO = new RoleVO();
        roleVO.setRoleName("admin2");
        roleVO.setActive(Boolean.TRUE);
        roleVO.setAuthorizable(Boolean.FALSE);
        roleVO.setActive(Boolean.TRUE);
        roleVO.setEffectiveTime(new Date());
        roleVO.setExpireTime(new Date());
        roleVO.setCreateBy("admin");
        roleVO.setExpireTime(new Date());
        roleService.addOrUpdateRoleInfo(userName,roleVO);

    }
}
