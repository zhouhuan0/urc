package com.yks.urc.motan.service.impl;

import com.yks.mq.utils.KafkaProducerSingleton;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.service.BaseServiceTest;
import com.yks.urc.vo.*;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
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
        map.put("operator",operator);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        System.out.println(json);
        resultVO = service.getPlatformList(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void getShopList() throws Exception {
        map.put("operator",operator);
        map.put("platform","亚马逊");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        System.out.println(json);
        resultVO = service.getShopList(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void checkDuplicateRoleName() throws Exception {
    }

    @Test
    public void copyRole() throws Exception {
        map.put("newRoleName", "adminTest4");
        map.put("sourceRoleId", "1532146669816000034");
        map.put("operator", operator);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        System.out.println(json);
        resultVO = service.copyRole(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void getAllFuncPermit() throws Exception {
        map.put("operator", "huangpeiqin");
       // map.put("ticket","d49b892e591dc3e098fd02f34410e5f5");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        System.out.println(json);
        resultVO = service.getAllFuncPermit(json);
        System.out.println(StringUtility.toJSONString(resultVO));
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

        List<String> lstRoleId =new ArrayList<>();
        lstRoleId.add("1538624216130000003");
        map.put("operator", "songguanye");
        map.put("lstRoleId", lstRoleId);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.getRolePermission(json);
        System.out.println(StringUtility.toJSONString(resultVO));
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
        roleVO.roleName = "admin";
        map.put("role", roleVO);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.getRolesByInfo(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void addOrUpdateRoleInfo() throws Exception {
        map.put("operator", "panyun");
        PermissionVO permissionVO =new PermissionVO();
        List<PermissionVO> permissionVOS =new ArrayList<>();
        permissionVO.setSysKey("000");
        permissionVO.setSysContext("{\"menu\":[{\"key\":\"000-000001\",\"module\":[{\"function\":[],\"key\":\"000-000001-000001\",\"module\":[{\"function\":[],\"key\":\"000-000001-000001-000001\",\"name\":\"我的操作权限\",\"pageFullPathName\":\"\",\"show\":0,\"url\":\"/permissionlist/\"}],\"name\":\"数据走势\",\"pageFullPathName\":\"\",\"show\":1,\"url\":\"/\"}],\"name\":\"首页\",\"url\":\"/\"}],\"system\":{\"key\":\"000\",\"name\":\"首页\",\"url\":\"/\"}}");
        permissionVOS.add(permissionVO);
        RoleVO roleVO = new RoleVO();
        roleVO.roleName="test_lwx3";
        roleVO.setRemark("test");
        roleVO.setSelectedContext(permissionVOS);
       // roleVO.roleId="1539221185095000011";
        roleVO.isForever = true;
        roleVO.setActive(Boolean.TRUE);
        roleVO.setAuthorizable(Boolean.FALSE);
        roleVO.setEffectiveTime(new Date());
        roleVO.setExpireTime(new Date());
        roleVO.setCreateBy("panyun");
        roleVO.setExpireTime(new Date());
        roleVO.lstUserName =new ArrayList<>();
        roleVO.lstUserName.add("houyunfeng");
        roleVO.lstUserName.add("huangjianfeng");
        roleVO.lstOwner = new ArrayList<>();
        roleVO.lstOwner.add("linwanxian");
        roleVO.lstOwner.add("panyun");

        map.put("role", roleVO);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        System.out.println(json);
        resultVO = service.addOrUpdateRoleInfo(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void getRoleByRoleId() throws Exception {
        map.put("operator", operator);
        map.put("roleId", "1539221477917000013");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        System.out.println(json);
        resultVO = service.getRoleByRoleId(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void deleteRoles() throws Exception {
        map.put("operator", operator);
        List<String> lstRoleId =new ArrayList<>();
        lstRoleId.add("1539221430345000012");
        map.put("lstRoleId",lstRoleId);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        System.out.println(json);
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
        map.put("operator","linwanxian");
        String json =StringUtility.toJSONString(map);
        MotanSession.initialSession(json);

        System.out.println(json);
        System.out.println("START====================" +StringUtility.dt2Str(new Date(),"yyyy-MM-dd HH:mm:sss"));
        ResultVO resultVO =service.updateApiPrefixCache(json);
        System.out.println(StringUtility.toJSONString(resultVO));
        System.out.println("END====================" +StringUtility.dt2Str(new Date(),"yyyy-MM-dd HH:mm:sss"));
    }

    @Test
    public void getAmazonShop() throws Exception {
        map.put("operator", operator);
        map.put("platformId","亚马逊");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
       //resultVO = service.getAmazonShop(json);
        System.out.println();
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void appointPlatformShopSite() throws Exception {
        map.put("operator", operator);
        map.put("platformId","ebay");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.appointPlatformShopSite(json);
        System.out.println();
        System.out.println(StringUtility.toJSONString(resultVO));
    }
    @Test
    public void test_getPlatformShopByEntityCode(){
        Long startTime =StringUtility.getDateTimeNow().getTime();
        map.put("operator", operator);
        map.put("entityCode", "E_ArmShopAccount");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.getPlatformShopByEntityCode(json);
        System.out.println("返回结果为");
        System.out.println(StringUtility.toJSONString(resultVO));
        Long endTime =StringUtility.getDateTimeNow().getTime();
        System.out.println(String.format("花费的时间为:[%d]",endTime-startTime));
    }
    @Test
    public void test_kafka(){
        List<DataRuleSysVO> dataRuleSysVOS =new ArrayList<>();
        DataRuleSysVO sysVO =new DataRuleSysVO();
        ExpressionVO expressionVO =new ExpressionVO();
       /* ProducerRecord<String, String> arg0 = new ProducerRecord<String, String>("URC_USER_DATARULE_009", value);
        Callback arg1 = new Callback() {
            @Override
            public void onCompletion(RecordMetadata arg0, Exception arg1) {

            }
        };
        KafkaProducerSingleton.getInstance(null).send(arg0, arg1);*/
    }
    @Test
    public void test_meageJson(){
        //StringUtility.inputStream2String()
    }
}