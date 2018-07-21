package com.yks.urc.motan.service.impl;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.service.BaseServiceTest;
import com.yks.urc.vo.ResultVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class UrcServiceImplTestLwx extends BaseServiceTest {
    @Autowired
    private IUrcService service;
    private ResultVO resultVO;
    private Map map =new HashMap();
    private String operator ="linwanxian";
    private int pageData =20;
    private int pageNumber=1;
    private  String roleId ="1529635932385000003";
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
    }

    @Test
    public void fuzzySearchUsersByUserName() throws Exception {
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
    }

    @Test
    public void addOrUpdateRoleInfo() throws Exception {
    }

    @Test
    public void getRoleByRoleId() throws Exception {
    }

    @Test
    public void deleteRoles() throws Exception {
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
    }

    @Test
    public void syncShopSite() throws Exception {
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
    public void getAmazonShopPage() throws Exception{
        map.put("pageNumber",pageNumber);
        map.put("pageData",pageData);
        map.put("operator",operator);
        map.put("shopSystem","0FunHifanDE");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO=service.getAmazonShopPage(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }
}