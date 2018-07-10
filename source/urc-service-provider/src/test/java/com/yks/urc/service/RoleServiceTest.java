package com.yks.urc.service;

import com.alibaba.fastjson.JSONObject;
import com.yks.common.util.DateUtil;
import com.yks.urc.entity.RoleDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.service.api.IRoleService;
import com.yks.urc.vo.PermissionVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.RoleVO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.*;

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
        jsonObject.put("operator","lvchangrong");
        jsonObject.put("roleId",1530581467555000128L);
        ResultVO<RoleVO>  resultVO= roleService.getRoleByRoleId(jsonObject.toString());
        Assert.assertNotNull(resultVO);
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
    public void addOrUpdateRoleInfo() throws ParseException {
        JSONObject jsonObject = new JSONObject();
        RoleVO roleVO = new RoleVO();
//        roleVO.setRoleId(1529746076695000006L);
        roleVO.setRoleName("admin-update223");
        roleVO.setActive(Boolean.TRUE);
        roleVO.setAuthorizable(Boolean.TRUE);
        roleVO.setForever(Boolean.FALSE);
        roleVO.setEffectiveTime(DateUtil.String2Date("2018-06-25 12:00:00",DateUtil.YYYY_MM_DD_HH_MM_SS));
        roleVO.setExpireTime(DateUtil.String2Date("2018-06-27 12:00:00",DateUtil.YYYY_MM_DD_HH_MM_SS));
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
        jsonObject.put("operator", "lvchangrong2");
        String jsonStr = jsonObject.toString();
        ResultVO resultVO = roleService.addOrUpdateRoleInfo(jsonStr);

        Assert.assertNotNull(resultVO);
    }
    @Test
    public void getRolesByInfo(){
        JSONObject jsonObject = new JSONObject();
        RoleVO roleVO = new RoleVO();
        StringBuilder roleNames = new StringBuilder();
        /*roleNames.append("admin-hehe").append(System.getProperty("line.separator"))
                .append("admin-update");
        roleVO.setRoleName(roleNames.toString());*/
        roleNames.append("admin");
        roleNames.append(System.getProperty("line.separator")).append(System.getProperty("line.separator"))
                .append(System.getProperty("line.separator"));
        //roleNames.append("admin");
        //roleVO.setRoleName("line.separator"+"line.separator+"+"line.separator");
        roleVO.setRoleName(roleNames.toString());
        jsonObject.put("operator","lvchangrong");
        jsonObject.put("pageNumber",1);
        jsonObject.put("pageData",8);
        jsonObject.put("role",roleVO);
        ResultVO resultVO = roleService.getRolesByInfo(jsonObject.toString());
        System.out.println(resultVO);
    }

    @Test
    public void copyRole(){
        roleService.copyRole("dcadmin","复制角色","1529550145551000001");
    }
}
