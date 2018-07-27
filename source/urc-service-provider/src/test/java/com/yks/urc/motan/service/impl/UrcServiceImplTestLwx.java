package com.yks.urc.motan.service.impl;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.service.BaseServiceTest;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.RoleOwnerVO;
import com.yks.urc.vo.RoleVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.Assert.*;

public class UrcServiceImplTestLwx extends BaseServiceTest {
    @Autowired
    private IUrcService service;
    private ResultVO resultVO;
    private Map map = new HashMap();
    private String operator = "linwanxian";
    private int pageData = 20;
    private int pageNumber = 1;
    private String roleId = "1529635932385000003";

    @Test
    public void syncUserInfo() throws Exception {

    }

    @Test
    public void login() throws Exception {
    }

    @Test
    public void syncDingOrgAndUser() throws Exception {
    }

    @Test
    public void getUserByDingOrgId() throws Exception {
    }

    @Test
    public void getUserByUserInfo() throws Exception {
    }

    @Test
    public void getAllOrgTree() throws Exception {
    }

    @Test
    public void getUsersByUserInfo() throws Exception {
    }

    @Test
    public void assignDataRuleTempl2User() throws Exception {
    }

    @Test
    public void getDataRuleTemplByTemplId() throws Exception {
    }

    @Test
    public void getDataRuleTempl() throws Exception {
    }

    @Test
    public void getPlatformList() throws Exception {
    }

    @Test
    public void getShopList() throws Exception {
    }

    @Test
    public void checkDuplicateRoleName() throws Exception {
    }

    @Test
    public void copyRole() throws Exception {
        map.put("newRoleName", "adminTest");
        map.put("sourceRoleId", "1531981633151000028");
        map.put("operator", operator);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.copyRole(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void getAllFuncPermit() throws Exception {
    }

    @Test
    public void funcPermitValidate() throws Exception {
    }

    @Test
    public void getUserByRoleId() throws Exception {
    }

    @Test
    public void getRoleUser() throws Exception {
    }

    @Test
    public void getMyDataRuleTempl() throws Exception {
    }

    @Test
    public void getDataRuleByUser() throws Exception {
    }

    @Test
    public void importSysPermit() throws Exception {
    }

    @Test
    public void getUserAuthorizablePermission() throws Exception {
    }

    @Test
    public void getRolePermission() throws Exception {
        map.put("operator", operator);

    }

    @Test
    public void getUserByUserName() throws Exception {
    }

    @Test
    public void getMyAuthWay() throws Exception {
        map.put("operator", operator);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.getMyAuthWay(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void fuzzySearchUsersByUserName() throws Exception {
        map.put("name", "pei");
        map.put("operator", operator);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.fuzzSearchPersonByName(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }

    @Test
    public void updateUsersOfRole() throws Exception {
    }

    @Test
    public void updateRolePermission() throws Exception {
    }

    @Test
    public void logout() throws Exception {
    }

    @Test
    public void getMavenPackageTime() throws Exception {
    }

    @Test
    public void addOrUpdateDataRuleTempl() throws Exception {
    }

    @Test
    public void deleteDataRuleTempl() throws Exception {
    }

    @Test
    public void checkDuplicateTemplName() throws Exception {
    }

    @Test
    public void getUserPermissionList() throws Exception {
    }

    @Test
    public void addOrUpdateDataRule() throws Exception {
    }

    @Test
    public void getRolesByInfo() throws Exception {
        map.put("pageNumber", pageNumber);
        map.put("pageData", pageData);
        map.put("operator", operator);
        RoleVO roleVO = new RoleVO();
        roleVO.roleName = "URC";
        map.put("role", roleVO);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.getRolesByInfo(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void addOrUpdateRoleInfo() throws Exception {
        map.put("operator", operator);
        RoleVO roleVO = new RoleVO();
        roleVO.setRoleName("admin2");
        roleVO.isForever = true;
        roleVO.setActive(Boolean.TRUE);
        roleVO.setAuthorizable(Boolean.FALSE);
        roleVO.setEffectiveTime(new Date());
        roleVO.setExpireTime(new Date());
        roleVO.setCreateBy("admin");
        roleVO.setExpireTime(new Date());
        roleVO.lstOwner = new ArrayList<>();
        RoleOwnerVO ownerVO = new RoleOwnerVO();
        ownerVO.owner = "zhangqinghui";
        roleVO.lstOwner.add(ownerVO);
        map.put("role", roleVO);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.addOrUpdateRoleInfo(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void getRoleByRoleId() throws Exception {
        map.put("operator", operator);
        map.put("roleId", "1529635932385000003");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.getRoleByRoleId(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void deleteRoles() throws Exception {
        map.put("operator", operator);
        List<String> lstRoleId =new ArrayList<>();
        lstRoleId.add("1532145741556000031");
        map.put("lstRoleId",lstRoleId);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.deleteRoles(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void assignAllPermit2Role() throws Exception {
    }

    @Test
    public void startMonitorMemory() throws Exception {
    }

    @Test
    public void endMonitorMemory() throws Exception {
    }

    @Test
    public void handleExpiredRole() throws Exception {
    }

    @Test
    public void updateUserPermitCache() throws Exception {
    }

    @Test
    public void operIsSuperAdmin() throws Exception {
    }

    @Test
    public void getPlatformShopSite() throws Exception {
    }

    @Test
    public void syncPlatform() throws Exception {
        map.put("operator", operator);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.syncPlatform(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void syncShopSite() throws Exception {
        map.put("operator", operator);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.syncShopSite(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void fuzzSearchPersonByName() throws Exception {
    }

    @Test
    public void getDataRuleGtDt() throws Exception {
    }

    @Test
    public void updateApiPrefixCache() throws Exception {
    }

    @Test
    public void getAmazonShop() throws Exception {
        map.put("operator", operator);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.getAmazonShop(json);
        System.out.println();
        System.out.println(StringUtility.toJSONString(resultVO));
    }
}