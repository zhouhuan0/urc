package com.yks.urc.service;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.entity.RoleDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.service.api.IRoleService;
import com.yks.urc.vo.PermissionVO;
import com.yks.urc.vo.ResultVO;
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
        List<Long> ids = new ArrayList();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lstRoleId",ids);
        jsonObject.put("operator","admin");
        roleService.deleteRoles(jsonObject.toString());
    }

    @Test
    public void getRoleByRoleId() {
        JSONObject  jsonObject = new JSONObject();
        jsonObject.put("operator","admin");
        jsonObject.put("roleId",1);
        ResultVO<RoleVO>  resultVO= roleService.getRoleByRoleId(jsonObject.toString());
        Assert.assertNull(resultVO);
    }

    @Test
    public void insertOrUpdate() {
        String userName = "admin";
        RoleVO roleVO = new RoleVO();
        roleVO.setRoleName("admin2");
        roleVO.setActive(Boolean.TRUE);
        roleVO.setAuthorizable(Boolean.FALSE);
        roleVO.setActive(Boolean.TRUE);
        roleVO.setEffectiveTime(new Date());
        roleVO.setExpireTime(new Date());
        roleVO.setCreateBy("admin");
        roleVO.setExpireTime(new Date());
        // roleService.addOrUpdateRoleInfo(userName, roleVO);

    }


    @Test
    public void addOrUpdateRoleInfo() {
        JSONObject jsonObject = new JSONObject();
        RoleVO roleVO = new RoleVO();
//        roleVO.setRoleId(1529399514060L);
        roleVO.setRoleName("admin");
        roleVO.setActive(Boolean.TRUE);
        roleVO.setAuthorizable(Boolean.TRUE);
        roleVO.setForever(Boolean.TRUE);
        roleVO.setEffectiveTime(new Date());
        roleVO.setExpireTime(new Date());
        roleVO.setRemark("我的备注");
        List<PermissionVO> permissionVOS = new ArrayList<>();
        PermissionVO permissionVO1 = new PermissionVO();
        permissionVO1.setSysKey("001");
        permissionVO1.setSysContext("{dfasdf}");
        PermissionVO permissionVO2 = new PermissionVO();
        permissionVO2.setSysKey("002");
        permissionVO2.setSysContext("{dfasdf_002}");
        permissionVOS.add(permissionVO1);
        permissionVOS.add(permissionVO2);
        roleVO.setSelectedContext(permissionVOS);
        List<String> lstUserName = new ArrayList<>();
        String userName = "admin";
        String userName2= "edison";
        lstUserName.add(userName);
        lstUserName.add(userName2);
        roleVO.setLstUserName(lstUserName);

        jsonObject.put("role", roleVO);
        jsonObject.put("operator", "admin");
        String jsonStr = jsonObject.toString();
        ResultVO resultVO = roleService.addOrUpdateRoleInfo(jsonStr);

        Assert.assertNotNull(resultVO);
    }
    @Test
    public void getRolesByInfo(){
        JSONObject jsonObject = new JSONObject();
        RoleVO roleVO = new RoleVO();
        jsonObject.put("operator","admin");
        jsonObject.put("pageNumber",1);
        jsonObject.put("pageData",4);
        ResultVO resultVO = roleService.getRolesByInfo(jsonObject.toString());
        System.out.println(resultVO);
    }

    @Test
    public void copyRole(){
        roleService.copyRole("dcadmin","复制角色","1529550145551000001");
    }
}
