package com.yks.urc.motan.service.impl;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.service.BaseServiceTest;
import com.yks.urc.vo.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.*;

public class UrcServiceImplTestLwx extends BaseServiceTest {
    @Autowired
    private IUrcService service;
    private ResultVO resultVO;
    private Map map = new HashMap();
    private Map dataMap = new HashMap();
    private String operator = "linwanxian";
    private int pageData = 20;
    private int pageNumber = 1;
    private String roleId = "1529635932385000003";

    @Test
    public void syncUserInfo() throws Exception {

    }

    @Test
    public void login() throws Exception {
        map.put("userName", "songguanye");
        map.put("pwd", "670317483sgy??");
        map.put("ip", "192.168.93.176");
        map.put("deviceName", "Chrome浏览器");
        System.out.println(StringUtility.toJSONString(map));
        ResultVO<LoginRespVO>  resultVO = service.login(map);
        System.out.println(StringUtility.toJSONString(resultVO));
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
        map.put("operator", operator);
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        System.out.println(json);
        resultVO = service.getPlatformList(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void getShopList() throws Exception {
        map.put("operator", operator);
        map.put("platform", "亚马逊");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        System.out.println(json);
        resultVO = service.getShopList(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void checkDuplicateRoleName() throws Exception {
        String json = "{\"newRoleName\":\"开发环境专用权限,勿动\",\"roleId\":\"1546589709303000002\",\"ticket\":\"0ebac79b03605693d7e3613274bd209a\",\"operator\":\"songguanye\",\"personName\":\"songguanye\",\"funcVersion\":\"34bf269604f019931cef4e39d0c54fab\",\"moduleUrl\":\"/user/rolemanagement/addupdaterole/\",\"deviceName\":\"Chrome浏览器\"}";
        MotanSession.initialSession(json);
        System.out.println(service.checkDuplicateRoleName(json));

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
        String strJson1 = StringUtility.inputStream2String(ClassLoader.getSystemResourceAsStream("getRoleUser.json"));
        MotanSession.initialSession(strJson1);
        resultVO =service.getRoleUser(strJson1);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void getMyDataRuleTempl() throws Exception {
    }

    @Test
    public void getDataRuleByUser() throws Exception {
    }

    @Test
    public void importSysPermit() throws Exception {
        String strJson1 = StringUtility.inputStream2String(ClassLoader.getSystemResourceAsStream("oms.json"));
        MotanSession.initialSession(strJson1);
//        resultVO =service.importSysPermit(strJson1);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void getUserAuthorizablePermission() throws Exception {
        map.put("operator","huanghongfei");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO=service.getUserAuthorizablePermission(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void getRolePermission() throws Exception {

        List<String> lstRoleId = new ArrayList<>();
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
        List<RoleVO> roleVOS = new ArrayList<>();
        RoleVO roleVO = new RoleVO();
        roleVO.roleId = "1539595765302000077";
        roleVO.lstUserName = new ArrayList<>();
        roleVO.lstUserName.add("test333");
        roleVOS.add(roleVO);
        map.put("lstRole", roleVOS);
        map.put("operator", "test");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO=service.updateUsersOfRole(json);
        System.out.println(StringUtility.toJSONString(resultVO));
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
//        String json = StringUtility.inputStream2String(new FileInputStream(new File("F:\\feature\\urc-func_dev\\urcenter\\source\\urc-service-provider\\src\\test\\resources\\addDataRule1.json")));
        String json = "";
        System.out.println(json);
        resultVO = service.addOrUpdateDataRule(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void getRolesByInfo() throws Exception {
        map.put("pageNumber", pageNumber);
        map.put("pageData", 100);
        map.put("operator", operator);
        map.put("isActive",1);
        map.put("isAdmin",0);
        map.put("searchType",2);
        map.put("searchContent","linwanxian");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.getRolesByInfo(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void addOrUpdateRoleInfo() throws Exception {

        String json = "";
        MotanSession.initialSession(json);
        resultVO = service.addOrUpdateRoleInfo(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void getRoleByRoleId() throws Exception {
        map.put("operator", operator);
        map.put("roleId", "1546939778294000003");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        System.out.println(json);
        resultVO = service.getRoleByRoleId(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void deleteRoles() throws Exception {
        String json = "{\"lstRoleId\":[\"1547177430854000004\"],\"ticket\":\"664281f3fe93e77a12316f72294eb9d7\",\"operator\":\"songguanye\",\"funcVersion\":\"3fe890a9611eab0f81226bc7215710b4\",\"moduleUrl\":\"/user/rolemanagement/\",\"personName\":\"songguanye\",\"deviceName\":\"Chrome浏览器\"}";
        resultVO = service.deleteRoles(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void assignAllPermit2Role() throws Exception {
        map.put("roleId","1529635932385000001");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        System.out.println(json);
        resultVO = service.assignAllPermit2Role(json);
        System.out.println(StringUtility.toJSONString(resultVO));
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
        map.put("name","pan");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.fuzzSearchPersonByName(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void getDataRuleGtDt() throws Exception {
    }

    @Test
    public void updateApiPrefixCache() throws Exception {
        map.put("operator", "linwanxian");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);

        System.out.println(json);
        System.out.println("START====================" + StringUtility.dt2Str(new Date(), "yyyy-MM-dd HH:mm:sss"));
        ResultVO resultVO = service.updateApiPrefixCache(json);
        System.out.println(StringUtility.toJSONString(resultVO));
        System.out.println("END====================" + StringUtility.dt2Str(new Date(), "yyyy-MM-dd HH:mm:sss"));
    }

    @Test
    public void getAmazonShop() throws Exception {
        map.put("operator", operator);
        map.put("platformId", "亚马逊");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        //resultVO = service.getAmazonShop(json);
        System.out.println();
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void appointPlatformShopSite() throws Exception {
        map.put("operator", operator);
        map.put("platformId", "ebay");
        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.appointPlatformShopSite(json);
        System.out.println();
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void test_getPlatformShopByEntityCode() {
        Long startTime = StringUtility.getDateTimeNow().getTime();
        map.put("operator", operator);
        map.put("entityCode", "E_CsOrg");
        //map.put("entityCode", "E_CustomerService");

        String json = StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        resultVO = service.getPlatformShopByEntityCode(json);
        System.out.println("返回结果为");
        System.out.println(StringUtility.toJSONString(resultVO));
        Long endTime = StringUtility.getDateTimeNow().getTime();
        System.out.println(String.format("花费的时间为:[%d]", endTime - startTime));
       outToFile(StringUtility.toJSONString(resultVO), "platform.json");
    }

    @Test
    public void test() {
        String jsonStr = "{\"userName\":\"songguanye\",\n" +
                "\n" +
                "\"mobile\":\"18376740674\", \n" +
                "\n" +
                "\"verificationCode\": 7415,\n" +
                "\n" + "\"newPwd\": \"670317483sgy???\" \n" +
                "\t\n" +
                "}";
        service.resetPwdSubmit(jsonStr);
    }
    @Test
    public  void test2(){
        map.put("userName","songguanye");
        map.put("mobile","18376740674");
        map.put("get_code","true");
       String json = StringUtility.toJSONString(map);
        service.resetPwdGetVerificationCode(json);

    }
    /**
     *  写出到文件
     * @param
     * @return
     * @Author lwx
     * @Date 2018/10/16 15:46
     */
    public static void outToFile(String str, String filePath) {
        File file = new File("F:\\Gitrepository\\urcenter\\source\\urc-service-provider\\src\\test\\resources\\" + filePath);
        BufferedWriter writer = null;
        //文件不存在
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "utf-8"));
            writer.write(str);
            writer.flush();
            writer.close();
        } catch (Exception e) {

        }
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
       String  test ="   abc  def    ";
       String str =null;
        //System.out.println(str.trim());
       // System.out.println(StringUtils.trim(str));
        System.out.println(StringUtility.trimPattern_Private(test,"\\s"));
    }

    @Test
    public void test_deleteNode(){
        Set<String> delKeys =new HashSet<>();
        delKeys.add("015-000001");
        dataMap.put("sysKey","015");
        dataMap.put("delKeys",delKeys);
        map.put("data",dataMap);
        map.put("operator",operator);
        String json =StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        System.out.println(json);
        resultVO=service.deleteSysPermitNode(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void test_updateNode(){
        dataMap.put("sysKey","015");
       List<NodeVO> updateNode =new ArrayList<>();
        NodeVO nodeVO =new NodeVO();
        nodeVO.key="015-000002-000003-002";
        nodeVO.name="新增/编辑TestTest";
        nodeVO.url="";
        updateNode.add(nodeVO);
        dataMap.put("updateNode",updateNode);
        map.put("data",dataMap);
        map.put("operator",operator);
        String json =StringUtility.toJSONString(map);
        MotanSession.initialSession(json);
        System.out.println(json);
        resultVO=service.updateSysPermitNode(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }
    @Test
    public void testGetPlatformShopByEntityCode(){
        String json = "{\n" +
                "\"operator\":\"songguanye\",\n" +
                "\"entityCode\":\"E_PlatformShopSite\"\n" +
                "}";
        MotanSession.initialSession(json);
        service.getPlatformShopByEntityCode(json);
    }
    @Test
    public void test_searchUserPerson() {
        String string = "{\n" +
                "\t\"data\":{\n" +
                "\t\t\"searchContext\":\"\",\n" +
                "\t\t\"pageData\":10,\n" +
                "\t\t\"pageNumber\":0\n" +
                "\t},\n" +
                "    \"ticket\":\"e883f46659e7a4d4e6e804778c9ecedf\",\n" +
                "    \"operator\":\"tangjianbo\",\n" +
                "    \"funcVersion\":\"17cf4017ca9212b53d96d172e94a2177\",\n" +
                "    \"moduleUrl\":\"/order/\",\n" +
                "    \"personName\":\"tangjianbo\",\n" +
                "    \"deviceName\":\"Chrome浏览器\"\n" +
                "}";
        System.out.println(StringUtility.toJSONString(service.searchUserPerson(string)));
    }

    }
