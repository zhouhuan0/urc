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
        resultVO =service.importSysPermit(strJson1);
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
        map.put("pageData", pageData);
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

        String json = "{\"role\":{\"isActive\":true,\"isAuthorizable\":false,\"isForever\":true,\"roleName\":\"杨波测试\",\"remark\":\"\",\"effectiveTime\":\"\",\"expireTime\":\"\",\"roleId\":\"1547612297943000005\",\"selectedContext\":[{\"sysKey\":\"001\",\"sysContext\":\"{\\\"menu\\\":[{\\\"key\\\":\\\"001-000006\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000006-000001-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"001-000006-000001\\\",\\\"module\\\":[],\\\"name\\\":\\\"订单分析\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/analysis/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000006-000002-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"001-000006-000002\\\",\\\"module\\\":[],\\\"name\\\":\\\"SKU分析\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/analysissku/\\\"}],\\\"name\\\":\\\"业绩看板\\\",\\\"url\\\":\\\"/order/\\\"},{\\\"key\\\":\\\"001-000001\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000001-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000001-000001-006\\\",\\\"name\\\":\\\"查看详情\\\"},{\\\"key\\\":\\\"001-000001-000001-002\\\",\\\"name\\\":\\\"批量标记\\\"},{\\\"key\\\":\\\"001-000001-000001-003\\\",\\\"name\\\":\\\"订单抓取\\\"},{\\\"key\\\":\\\"001-000001-000001-004\\\",\\\"name\\\":\\\"标记跟踪号\\\"},{\\\"key\\\":\\\"001-000001-000001-005\\\",\\\"name\\\":\\\"同步订单\\\"},{\\\"key\\\":\\\"001-000001-000001-007\\\",\\\"name\\\":\\\"订单导出\\\"}],\\\"key\\\":\\\"001-000001-000001\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000001-000001-000001-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"001-000001-000001-000001\\\",\\\"name\\\":\\\"速卖通订单详情\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":0,\\\"url\\\":\\\"/order/platformorder/smt/smtorderdetail/\\\"}],\\\"name\\\":\\\"AliExpress订单\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/platformorder/smt/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000001-000003-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000001-000003-002\\\",\\\"name\\\":\\\"批量标记\\\"},{\\\"key\\\":\\\"001-000001-000003-003\\\",\\\"name\\\":\\\"订单抓取\\\"},{\\\"key\\\":\\\"001-000001-000003-004\\\",\\\"name\\\":\\\"订单导出\\\"},{\\\"key\\\":\\\"001-000001-000003-005\\\",\\\"name\\\":\\\"标记跟踪号\\\"},{\\\"key\\\":\\\"001-000001-000003-006\\\",\\\"name\\\":\\\"同步订单\\\"}],\\\"key\\\":\\\"001-000001-000003\\\",\\\"module\\\":[],\\\"name\\\":\\\"Wish订单\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/platformorder/wish/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000001-000004-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000001-000004-002\\\",\\\"name\\\":\\\"订单抓取\\\"},{\\\"key\\\":\\\"001-000001-000004-003\\\",\\\"name\\\":\\\"订单导出\\\"},{\\\"key\\\":\\\"001-000001-000004-004\\\",\\\"name\\\":\\\"标记跟踪号\\\"},{\\\"key\\\":\\\"001-000001-000004-005\\\",\\\"name\\\":\\\"同步订单\\\"},{\\\"key\\\":\\\"001-000001-000004-006\\\",\\\"name\\\":\\\"查看详情\\\"}],\\\"key\\\":\\\"001-000001-000004\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000001-000004-000001-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"001-000001-000004-000001\\\",\\\"name\\\":\\\"Ebay订单详情\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":0,\\\"url\\\":\\\"/order/platformorder/ebay/detail/\\\"}],\\\"name\\\":\\\"Ebay订单\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/platformorder/ebay/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000001-000005-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000001-000005-006\\\",\\\"name\\\":\\\"查看详情\\\"},{\\\"key\\\":\\\"001-000001-000005-002\\\",\\\"name\\\":\\\"批量标记\\\"},{\\\"key\\\":\\\"001-000001-000004-003\\\",\\\"name\\\":\\\"订单抓取\\\"},{\\\"key\\\":\\\"001-000001-000005-004\\\",\\\"name\\\":\\\"标记跟踪号\\\"},{\\\"key\\\":\\\"001-000001-000005-005\\\",\\\"name\\\":\\\"同步订单\\\"},{\\\"key\\\":\\\"001-000001-000005-007\\\",\\\"name\\\":\\\"订单导出\\\"}],\\\"key\\\":\\\"001-000001-000005\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000001-000005-000001-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"001-000001-000005-000001\\\",\\\"name\\\":\\\"Joom订单详情\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":0,\\\"url\\\":\\\"/order/platformorder/joom/detail/\\\"}],\\\"name\\\":\\\"Joom订单\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/platformorder/joom/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000001-000006-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000001-000006-006\\\",\\\"name\\\":\\\"查看详情\\\"},{\\\"key\\\":\\\"001-000001-000006-002\\\",\\\"name\\\":\\\"批量标记\\\"},{\\\"key\\\":\\\"001-000001-000006-003\\\",\\\"name\\\":\\\"订单抓取\\\"},{\\\"key\\\":\\\"001-000001-000006-004\\\",\\\"name\\\":\\\"标记跟踪号\\\"},{\\\"key\\\":\\\"001-000001-000006-005\\\",\\\"name\\\":\\\"同步订单\\\"},{\\\"key\\\":\\\"001-000001-000006-007\\\",\\\"name\\\":\\\"订单导出\\\"}],\\\"key\\\":\\\"001-000001-000006\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000001-000006-000001-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"001-000001-000006-000001\\\",\\\"name\\\":\\\"Amazon订单详情\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":0,\\\"url\\\":\\\"/order/platformorder/amazon/detail/\\\"}],\\\"name\\\":\\\"Amazon订单\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/platformorder/amazon/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000001-000007-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000001-000007-002\\\",\\\"name\\\":\\\"订单抓取\\\"},{\\\"key\\\":\\\"001-000001-000007-003\\\",\\\"name\\\":\\\"标记跟踪号\\\"},{\\\"key\\\":\\\"001-000001-000007-004\\\",\\\"name\\\":\\\"同步订单\\\"},{\\\"key\\\":\\\"001-000001-000007-005\\\",\\\"name\\\":\\\"查看详情\\\"}],\\\"key\\\":\\\"001-000001-000007\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000001-000007-000001-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"001-000001-000007-000001\\\",\\\"name\\\":\\\"Mymall订单详情\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":0,\\\"url\\\":\\\"/order/platformorder/mymall/detail/\\\"}],\\\"name\\\":\\\"Mymall订单\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/platformorder/mymall/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000001-000008-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000001-000008-002\\\",\\\"name\\\":\\\"订单抓取\\\"},{\\\"key\\\":\\\"001-000001-000008-003\\\",\\\"name\\\":\\\"标记跟踪号\\\"},{\\\"key\\\":\\\"001-000001-000008-004\\\",\\\"name\\\":\\\"同步订单\\\"},{\\\"key\\\":\\\"001-000001-000008-005\\\",\\\"name\\\":\\\"查看详情\\\"}],\\\"key\\\":\\\"001-000001-000008\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000001-000008-000001-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"001-000001-000008-000001\\\",\\\"name\\\":\\\"Shopee订单详情\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":0,\\\"url\\\":\\\"/order/platformorder/shopee/detail/\\\"}],\\\"name\\\":\\\"Shopee订单\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/platformorder/shopee/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000001-000002-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000001-000002-002\\\",\\\"name\\\":\\\"导入\\\"}],\\\"key\\\":\\\"001-000001-000002\\\",\\\"module\\\":[],\\\"name\\\":\\\"订单导入\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/platformorder/orderimport/\\\"}],\\\"name\\\":\\\"平台订单\\\",\\\"url\\\":\\\"/order/\\\"},{\\\"key\\\":\\\"001-000002\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000002-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000002-000001-002\\\",\\\"name\\\":\\\"查看详情\\\"},{\\\"key\\\":\\\"001-000002-000001-003\\\",\\\"name\\\":\\\"导出订单列表\\\"}],\\\"key\\\":\\\"001-000002-000001\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000002-000001-000001-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"001-000002-000001-000001\\\",\\\"name\\\":\\\"全部订单详情\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":0,\\\"url\\\":\\\"/order/orderlist/orderdetail/\\\"}],\\\"name\\\":\\\"全部订单\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/orderlist/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000002-000002-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000002-000002-002\\\",\\\"name\\\":\\\"编辑\\\"},{\\\"key\\\":\\\"001-000002-000002-003\\\",\\\"name\\\":\\\"订单导出\\\"}],\\\"key\\\":\\\"001-000002-000002\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000002-000002-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000002-000002-000001-002\\\",\\\"name\\\":\\\"撤单\\\"},{\\\"key\\\":\\\"001-000002-000002-000001-003\\\",\\\"name\\\":\\\"修改收货地址\\\"},{\\\"key\\\":\\\"001-000002-000002-000001-004\\\",\\\"name\\\":\\\"我要留言\\\"},{\\\"key\\\":\\\"001-000002-000002-000001-005\\\",\\\"name\\\":\\\"新增商品\\\"},{\\\"key\\\":\\\"001-000002-000002-000001-006\\\",\\\"name\\\":\\\"编辑商品\\\"},{\\\"key\\\":\\\"001-000002-000002-000001-007\\\",\\\"name\\\":\\\"手工分仓\\\"}],\\\"key\\\":\\\"001-000002-000002-000001\\\",\\\"name\\\":\\\"异常订单详情\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":0,\\\"url\\\":\\\"/order/exceptionorderlist/exceptionorderdetail/\\\"}],\\\"name\\\":\\\"异常订单\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/exceptionorderlist/\\\"}],\\\"name\\\":\\\"订单管理\\\",\\\"url\\\":\\\"/order/\\\"},{\\\"key\\\":\\\"001-000003\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000003-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000003-000001-002\\\",\\\"name\\\":\\\"查看详情\\\"},{\\\"key\\\":\\\"001-000003-000001-003\\\",\\\"name\\\":\\\"订单导出\\\"},{\\\"key\\\":\\\"001-000003-000001-004\\\",\\\"name\\\":\\\"撤单\\\"}],\\\"key\\\":\\\"001-000003-000001\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000003-000001-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000003-000001-000001-002\\\",\\\"name\\\":\\\"物流面单\\\"}],\\\"key\\\":\\\"001-000003-000001-000001\\\",\\\"name\\\":\\\"包裹详情\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":0,\\\"url\\\":\\\"/order/deliveryparcellist/deliveryparceldetail/\\\"}],\\\"name\\\":\\\"全部包裹\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/deliveryparcellist/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000003-000002-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000003-000002-002\\\",\\\"name\\\":\\\"查看详情\\\"},{\\\"key\\\":\\\"001-000003-000002-003\\\",\\\"name\\\":\\\"批量审核\\\"},{\\\"key\\\":\\\"001-000003-000002-004\\\",\\\"name\\\":\\\"批量撤单\\\"},{\\\"key\\\":\\\"001-000003-000002-005\\\",\\\"name\\\":\\\"审核/撤单\\\"}],\\\"key\\\":\\\"001-000003-000002\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000003-000002-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000003-000002-000001-002\\\",\\\"name\\\":\\\"审核\\\"}],\\\"key\\\":\\\"001-000003-000002-000001\\\",\\\"name\\\":\\\"包裹详情\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":0,\\\"url\\\":\\\"/order/negativeprofitauditlist/negativeprofitauditdetailfail/\\\"}],\\\"name\\\":\\\"待审核包裹\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/negativeprofitauditlist/\\\"}],\\\"name\\\":\\\"包裹订单\\\",\\\"url\\\":\\\"/order/\\\"},{\\\"key\\\":\\\"001-000004\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000004-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000004-000001-002\\\",\\\"name\\\":\\\"字段配置\\\"},{\\\"key\\\":\\\"001-000004-000001-003\\\",\\\"name\\\":\\\"SKU解析配置\\\"},{\\\"key\\\":\\\"001-000004-000001-004\\\",\\\"name\\\":\\\"新增条件配置\\\"},{\\\"key\\\":\\\"001-000004-000001-005\\\",\\\"name\\\":\\\"编辑条件配置\\\"},{\\\"key\\\":\\\"001-000004-000001-006\\\",\\\"name\\\":\\\"删除条件配置\\\"}],\\\"key\\\":\\\"001-000004-000001\\\",\\\"module\\\":[],\\\"name\\\":\\\"抓单转换规则\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/conversion/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000004-000003-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000004-000003-002\\\",\\\"name\\\":\\\"查看详情\\\"},{\\\"key\\\":\\\"001-000004-000003-003\\\",\\\"name\\\":\\\"新增\\\"},{\\\"key\\\":\\\"001-000004-000003-004\\\",\\\"name\\\":\\\"编辑/删除\\\"}],\\\"key\\\":\\\"001-000004-000003\\\",\\\"module\\\":[],\\\"name\\\":\\\"指定仓规则\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/warehouselist/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000004-000004-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000004-000004-002\\\",\\\"name\\\":\\\"查看详情\\\"},{\\\"key\\\":\\\"001-000004-000004-003\\\",\\\"name\\\":\\\"新增\\\"},{\\\"key\\\":\\\"001-000004-000004-004\\\",\\\"name\\\":\\\"编辑/删除\\\"}],\\\"key\\\":\\\"001-000004-000004\\\",\\\"module\\\":[],\\\"name\\\":\\\"收货人信息拦截\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/orderconsignee/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000004-000006-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000004-000006-002\\\",\\\"name\\\":\\\"新增\\\"},{\\\"key\\\":\\\"001-000004-000006-003\\\",\\\"name\\\":\\\"编辑\\\"},{\\\"key\\\":\\\"001-000004-000006-004\\\",\\\"name\\\":\\\"删除\\\"}],\\\"key\\\":\\\"001-000004-000006\\\",\\\"module\\\":[],\\\"name\\\":\\\"指定SKU替换规则\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/skureplacementrules/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000004-000005-004\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000004-000005-001\\\",\\\"name\\\":\\\"新增\\\"},{\\\"key\\\":\\\"001-000004-000005-002\\\",\\\"name\\\":\\\"编辑\\\"},{\\\"key\\\":\\\"001-000004-000005-003\\\",\\\"name\\\":\\\"删除\\\"}],\\\"key\\\":\\\"001-000004-000005\\\",\\\"module\\\":[],\\\"name\\\":\\\"订单标记规则\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/markrules/\\\"}],\\\"name\\\":\\\"订单配置\\\",\\\"url\\\":\\\"/order/\\\"},{\\\"key\\\":\\\"001-000005\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000005-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000005-000001-002\\\",\\\"name\\\":\\\"导出\\\"},{\\\"key\\\":\\\"001-000005-000001-003\\\",\\\"name\\\":\\\"导入\\\"},{\\\"key\\\":\\\"001-000005-000001-004\\\",\\\"name\\\":\\\"跟踪号抓取配置\\\"}],\\\"key\\\":\\\"001-000005-000001\\\",\\\"name\\\":\\\"跟踪号管理\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/basicdata/tracknumbermanage/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000005-000002-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000005-000002-002\\\",\\\"name\\\":\\\"查看详情\\\"},{\\\"key\\\":\\\"001-000005-000002-003\\\",\\\"name\\\":\\\"新增\\\"},{\\\"key\\\":\\\"001-000005-000002-004\\\",\\\"name\\\":\\\"编辑\\\"},{\\\"key\\\":\\\"001-000005-000002-005\\\",\\\"name\\\":\\\"删除\\\"}],\\\"key\\\":\\\"001-000005-000002\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"001-000005-000002-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"001-000005-000002-000001-002\\\",\\\"name\\\":\\\"编辑渠道信息\\\"},{\\\"key\\\":\\\"001-000005-000002-000001-003\\\",\\\"name\\\":\\\"编辑仓库信息\\\"},{\\\"key\\\":\\\"001-000005-000002-000001-004\\\",\\\"name\\\":\\\"编辑第三方信息\\\"},{\\\"key\\\":\\\"001-000005-000002-000001-005\\\",\\\"name\\\":\\\"编辑标记信息\\\"},{\\\"key\\\":\\\"001-000005-000002-000001-006\\\",\\\"name\\\":\\\"删除\\\"}],\\\"key\\\":\\\"001-000005-000002-000001\\\",\\\"module\\\":[],\\\"name\\\":\\\"渠道管理详情\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":0,\\\"url\\\":\\\"/order/basicdata/channellist/channelsignall/\\\"}],\\\"name\\\":\\\"渠道管理\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/basicdata/channellist/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"001-000005-000003-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"001-000005-000003\\\",\\\"name\\\":\\\"导入导出记录\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/order/basicdata/importexportrecords/\\\"}],\\\"name\\\":\\\"基础数据\\\",\\\"url\\\":\\\"/order/\\\"}],\\\"system\\\":{\\\"key\\\":\\\"001\\\",\\\"name\\\":\\\"订单\\\",\\\"url\\\":\\\"/order/\\\"}}\"},{\"sysKey\":\"002\",\"sysContext\":\"{\\\"menu\\\":[{\\\"key\\\":\\\"002-000001\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000001-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000001-000001-002\\\",\\\"name\\\":\\\"驳回\\\"},{\\\"key\\\":\\\"002-000001-000001-003\\\",\\\"name\\\":\\\"撤销\\\"},{\\\"key\\\":\\\"002-000001-000001-004\\\",\\\"name\\\":\\\"删除\\\"},{\\\"key\\\":\\\"002-000001-000001-005\\\",\\\"name\\\":\\\"归档\\\"},{\\\"key\\\":\\\"002-000001-000001-006\\\",\\\"name\\\":\\\"审核\\\"},{\\\"key\\\":\\\"002-000001-000001-007\\\",\\\"name\\\":\\\"合并报关单\\\"},{\\\"key\\\":\\\"002-000001-000001-008\\\",\\\"name\\\":\\\"修改报关单号\\\"},{\\\"key\\\":\\\"002-000001-000001-009\\\",\\\"name\\\":\\\"导入报关单号\\\"},{\\\"key\\\":\\\"002-000001-000001-010\\\",\\\"name\\\":\\\"新建报关单\\\"},{\\\"key\\\":\\\"002-000001-000001-011\\\",\\\"name\\\":\\\"制单\\\"},{\\\"key\\\":\\\"002-000001-000001-012\\\",\\\"name\\\":\\\"查看制单数据\\\"}],\\\"key\\\":\\\"002-000001-000001\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000001-000001-000001-003\\\",\\\"name\\\":\\\"导出核对清单\\\"},{\\\"key\\\":\\\"002-000001-000001-000001-004\\\",\\\"name\\\":\\\"合并、删除和同步SKU\\\"},{\\\"key\\\":\\\"002-000001-000001-000001-005\\\",\\\"name\\\":\\\"核对清单修改\\\"},{\\\"key\\\":\\\"002-000001-000001-000001-006\\\",\\\"name\\\":\\\"修改资料\\\"},{\\\"key\\\":\\\"002-000001-000001-000001-007\\\",\\\"name\\\":\\\"核对清单排序\\\"},{\\\"key\\\":\\\"002-000001-000001-000001-008\\\",\\\"name\\\":\\\"生成报关资料\\\"}],\\\"key\\\":\\\"002-000001-000001-000001\\\",\\\"module\\\":[],\\\"name\\\":\\\"制单\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/declaration/order/create/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"002-000001-000001-000002-001\\\",\\\"name\\\":\\\"导出核对清单\\\"}],\\\"key\\\":\\\"002-000001-000001-000002\\\",\\\"module\\\":[],\\\"name\\\":\\\"查看制单数据\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/declaration/order/view/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"002-000001-000001-000003-001\\\",\\\"name\\\":\\\"报关草单\\\"},{\\\"key\\\":\\\"002-000001-000001-000003-002\\\",\\\"name\\\":\\\"发票\\\"},{\\\"key\\\":\\\"002-000001-000001-000003-003\\\",\\\"name\\\":\\\"装箱单\\\"},{\\\"key\\\":\\\"002-000001-000001-000003-004\\\",\\\"name\\\":\\\"合同\\\"},{\\\"key\\\":\\\"002-000001-000001-000003-005\\\",\\\"name\\\":\\\"载货清单(FBA)\\\"},{\\\"key\\\":\\\"002-000001-000001-000003-006\\\",\\\"name\\\":\\\"载货清单(海外仓)\\\"},{\\\"key\\\":\\\"002-000001-000001-000003-007\\\",\\\"name\\\":\\\"导出\\\"}],\\\"key\\\":\\\"002-000001-000001-000003\\\",\\\"module\\\":[],\\\"name\\\":\\\"查看报关资料\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/declaration/check_customs_info/\\\"}],\\\"name\\\":\\\"报关单\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/declaration/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"002-000001-000002-001\\\",\\\"name\\\":\\\"新增、修改和删除SKU税率\\\"},{\\\"key\\\":\\\"002-000001-000002-002\\\",\\\"name\\\":\\\"新增、修改汇率\\\"}],\\\"key\\\":\\\"002-000001-000002\\\",\\\"module\\\":[],\\\"name\\\":\\\"报关设置\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/customs_set/\\\"},{\\\"function\\\":[],\\\"key\\\":\\\"002-000001-000003\\\",\\\"module\\\":[],\\\"name\\\":\\\"操作日志\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/operate_log/\\\"}],\\\"name\\\":\\\"报关管理\\\",\\\"url\\\":\\\"/lgtconfig/\\\"},{\\\"key\\\":\\\"002-000002\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000002-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000002-000001-002\\\",\\\"name\\\":\\\"导出\\\"}],\\\"key\\\":\\\"002-000002-000001\\\",\\\"name\\\":\\\"轨迹查询\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/trajectory/query/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"002-000002-000002-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000002-000002-002\\\",\\\"name\\\":\\\"国内仓-查看\\\"},{\\\"key\\\":\\\"002-000002-000002-003\\\",\\\"name\\\":\\\"海外仓-查看\\\"}],\\\"key\\\":\\\"002-000002-000002\\\",\\\"name\\\":\\\"渠道设置\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/trajectory/channelset/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"002-000002-000003-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000002-000003-002\\\",\\\"name\\\":\\\"设置\\\"}],\\\"key\\\":\\\"002-000002-000003\\\",\\\"name\\\":\\\"轨迹状态设置\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/trajectory/channelstateset/\\\"}],\\\"name\\\":\\\"物流轨迹\\\",\\\"url\\\":\\\"/lgtconfig/\\\"},{\\\"key\\\":\\\"002-000004\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000004-000001-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000004-000001\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000004-000001-000001-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000004-000001-000001\\\",\\\"name\\\":\\\"核对明细\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/logisticsreconciliation/checkbill/checkdetail/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"002-000004-000001-000002-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000004-000001-000002\\\",\\\"name\\\":\\\"查看明细\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/logisticsreconciliation/checkbill/viewdetail/\\\"}],\\\"name\\\":\\\"核对账单\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/logisticsreconciliation/checkbill/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"002-000004-000002-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000004-000002\\\",\\\"name\\\":\\\"账单明细\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/logisticsreconciliation/billdetail/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"002-000004-000003-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000004-000003\\\",\\\"name\\\":\\\"对账规则设置\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/logisticsreconciliation/rulesetting/\\\"}],\\\"name\\\":\\\"物流对账\\\",\\\"url\\\":\\\"/lgtconfig/\\\"},{\\\"key\\\":\\\"002-000003\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000003-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000003-000001-002\\\",\\\"name\\\":\\\"编辑\\\"}],\\\"key\\\":\\\"002-000003-000001\\\",\\\"name\\\":\\\"物流服务商\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/servicemanage/provider/\\\"}],\\\"name\\\":\\\"物流商管理\\\",\\\"url\\\":\\\"/lgtconfig/\\\"},{\\\"key\\\":\\\"002-000005\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000005-000001-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000005-000001\\\",\\\"name\\\":\\\"导入导出队列\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/datamanagement/importexportqueue/\\\"}],\\\"name\\\":\\\"数据管理\\\",\\\"url\\\":\\\"/lgtconfig/\\\"}],\\\"system\\\":{\\\"key\\\":\\\"002\\\",\\\"name\\\":\\\"物流\\\",\\\"url\\\":\\\"/lgtconfig/\\\"}}\"},{\"sysKey\":\"005\",\"sysContext\":\"{\\\"menu\\\":[{\\\"key\\\":\\\"005-000001\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"005-000001-000001-001\\\",\\\"name\\\":\\\"新增\\\"},{\\\"key\\\":\\\"005-000001-000001-002\\\",\\\"name\\\":\\\"编辑\\\"},{\\\"key\\\":\\\"005-000001-000001-003\\\",\\\"name\\\":\\\"禁用\\\"},{\\\"key\\\":\\\"005-000001-000001-004\\\",\\\"name\\\":\\\"启用\\\"}],\\\"key\\\":\\\"005-000001-000001\\\",\\\"module\\\":[],\\\"name\\\":\\\"仓库设置\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/bd/depot/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"005-000001-000002-001\\\",\\\"name\\\":\\\"用户新增\\\"},{\\\"key\\\":\\\"005-000001-000002-002\\\",\\\"name\\\":\\\"用户编辑\\\"},{\\\"key\\\":\\\"005-000001-000002-003\\\",\\\"name\\\":\\\"用户禁用\\\"},{\\\"key\\\":\\\"005-000001-000002-004\\\",\\\"name\\\":\\\"用户启用\\\"},{\\\"key\\\":\\\"005-000001-000002-005\\\",\\\"name\\\":\\\"平台新增\\\"},{\\\"key\\\":\\\"005-000001-000002-006\\\",\\\"name\\\":\\\"平台编辑\\\"},{\\\"key\\\":\\\"005-000001-000002-007\\\",\\\"name\\\":\\\"用户组新增\\\"},{\\\"key\\\":\\\"005-000001-000002-008\\\",\\\"name\\\":\\\"用户组编辑\\\"}],\\\"key\\\":\\\"005-000001-000002\\\",\\\"module\\\":[],\\\"name\\\":\\\"新品用户\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/bd/user/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"005-000001-000003-001\\\",\\\"name\\\":\\\"新增\\\"},{\\\"key\\\":\\\"005-000001-000003-002\\\",\\\"name\\\":\\\"编辑\\\"}],\\\"key\\\":\\\"005-000001-000003\\\",\\\"module\\\":[],\\\"name\\\":\\\"意向供应商\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/bd/intent_supplier/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"005-000001-000004-001\\\",\\\"name\\\":\\\"新增\\\"},{\\\"key\\\":\\\"005-000001-000004-002\\\",\\\"name\\\":\\\"编辑\\\"},{\\\"key\\\":\\\"005-000001-000004-003\\\",\\\"name\\\":\\\"禁用\\\"},{\\\"key\\\":\\\"005-000001-000004-004\\\",\\\"name\\\":\\\"启用\\\"}],\\\"key\\\":\\\"005-000001-000004\\\",\\\"module\\\":[],\\\"name\\\":\\\"新品项目\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/bd/project_flow/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"005-000001-000005-001\\\",\\\"name\\\":\\\"新增\\\"},{\\\"key\\\":\\\"005-000001-000005-002\\\",\\\"name\\\":\\\"编辑\\\"},{\\\"key\\\":\\\"005-000001-000005-003\\\",\\\"name\\\":\\\"禁用\\\"},{\\\"key\\\":\\\"005-000001-000005-004\\\",\\\"name\\\":\\\"启用\\\"}],\\\"key\\\":\\\"005-000001-000005\\\",\\\"module\\\":[],\\\"name\\\":\\\"新品审核\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/bd/audit_flow/\\\"}],\\\"name\\\":\\\"基础设置\\\",\\\"url\\\":\\\"/npd/\\\"},{\\\"key\\\":\\\"005-000002\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"005-000002-000001-001\\\",\\\"name\\\":\\\"新增\\\"},{\\\"key\\\":\\\"005-000002-000001-002\\\",\\\"name\\\":\\\"批量分派\\\"},{\\\"key\\\":\\\"005-000002-000001-003\\\",\\\"name\\\":\\\"导入\\\"}],\\\"key\\\":\\\"005-000002-000001\\\",\\\"module\\\":[],\\\"name\\\":\\\"热销新品\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/pd/hnpdapply/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"005-000002-000002-001\\\",\\\"name\\\":\\\"新增\\\"},{\\\"key\\\":\\\"005-000002-000002-002\\\",\\\"name\\\":\\\"批量提交\\\"},{\\\"key\\\":\\\"005-000002-000002-003\\\",\\\"name\\\":\\\"批量生成交接单\\\"},{\\\"key\\\":\\\"005-000002-000002-004\\\",\\\"name\\\":\\\"批量审核\\\"}],\\\"key\\\":\\\"005-000002-000002\\\",\\\"module\\\":[{\\\"function\\\":[],\\\"key\\\":\\\"005-000002-000002-000001\\\",\\\"name\\\":\\\"新建立项\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/pd/project/create/\\\"},{\\\"function\\\":[],\\\"key\\\":\\\"005-000002-000002-000002\\\",\\\"name\\\":\\\"立项明细\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/pd/project/project_detail/\\\"},{\\\"function\\\":[],\\\"key\\\":\\\"005-000002-000002-000003\\\",\\\"name\\\":\\\"立项明细修改\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/pd/project/project_detail_edit/\\\"},{\\\"function\\\":[],\\\"key\\\":\\\"005-000002-000002-000004\\\",\\\"name\\\":\\\"审核界面\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/pd/project/review/\\\"},{\\\"function\\\":[],\\\"key\\\":\\\"005-000002-000002-000005\\\",\\\"name\\\":\\\"绑定供应商\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/pd/project/bindvendor/\\\"},{\\\"function\\\":[],\\\"key\\\":\\\"005-000002-000002-000006\\\",\\\"name\\\":\\\"对样\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/pd/project/check_specimen/\\\"}],\\\"name\\\":\\\"新品立项\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/pd/project/\\\"},{\\\"function\\\":[],\\\"key\\\":\\\"005-000002-000003\\\",\\\"module\\\":[],\\\"name\\\":\\\"样品表\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/pd/specimen/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"005-000002-000004-001\\\",\\\"name\\\":\\\"批量提交\\\"},{\\\"key\\\":\\\"005-000002-000004-002\\\",\\\"name\\\":\\\"批量审核\\\"}],\\\"key\\\":\\\"005-000002-000004\\\",\\\"module\\\":[{\\\"function\\\":[],\\\"key\\\":\\\"005-000002-000004-000001\\\",\\\"name\\\":\\\"交接明细\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/pd/handover/project_detail/\\\"}],\\\"name\\\":\\\"新品交接\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/pd/handover/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"005-000002-000005-001\\\",\\\"name\\\":\\\"导出SPU\\\"},{\\\"key\\\":\\\"005-000002-000005-002\\\",\\\"name\\\":\\\"导入SKU\\\"},{\\\"key\\\":\\\"005-000002-000005-003\\\",\\\"name\\\":\\\"批量确认\\\"}],\\\"key\\\":\\\"005-000002-000005\\\",\\\"module\\\":[],\\\"name\\\":\\\"生成SKU\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/pd/sku/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"005-000002-000006-001\\\",\\\"name\\\":\\\"合并首单申请\\\"}],\\\"key\\\":\\\"005-000002-000006\\\",\\\"module\\\":[],\\\"name\\\":\\\"新品跟踪\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/pd/track/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"005-000002-000008-001\\\",\\\"name\\\":\\\"批量审核\\\"}],\\\"key\\\":\\\"005-000002-000008\\\",\\\"module\\\":[{\\\"function\\\":[],\\\"key\\\":\\\"005-000002-000008-000001\\\",\\\"module\\\":[],\\\"name\\\":\\\"首单明细\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/pd/fapply/project_detail/\\\"}],\\\"name\\\":\\\"首单申请\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/npd/pd/fapply/\\\"}],\\\"name\\\":\\\"新品开发\\\",\\\"url\\\":\\\"/npd/\\\"}],\\\"system\\\":{\\\"key\\\":\\\"005\\\",\\\"name\\\":\\\"新品\\\",\\\"url\\\":\\\"/npd/\\\"}}\"}],\"lstUserName\":[\"yangwanmin\",\"sulin\",\"chenglifu\",\"panyun\",\"wujianghui\",\"chenglifu1\",\"lvchangrong\",\"wujianghui1\",\"oujie\",\"huangpeiqin\",\"houyunfeng\",\"panxi\",\"lujinwu\",\"liuyongren\",\"yangbo\",\"renmaohua\",\"tangfeng\",\"tangyong\",\"weijie\",\"chensi2\",\"chensi21\",\"xieyi2\",\"xieyi1\",\"mengyuhua\",\"liujun\",\"opsuser\",\"liuli\",\"chenbaoxiang1\",\"lingzhen\",\"liuxiaoliang\",\"hanhanzhou\",\"chenwenchun\",\"huangjianfeng\",\"liuxiaoliang1\",\"hanhanzhou1\",\"chenjiangxin\",\"zhangqinghui\",\"panzhengtao\",\"yichunlin\",\"chenlin\",\"sunyan\",\"wangyuanwen\",\"zhangguoping\",\"songchi\",\"xieyi21\",\"langyuting\",\"liuju2\",\"wangyuzhang\",\"hexiaopeng\",\"wangxuejing\",\"chenyi\",\"pengxuefang\",\"sulin2\",\"fenglong\",\"yanfukun\",\"zhangbingbing\",\"zhouhuan\",\"chenlintao\",\"chaozhaohui\",\"chencanwei\",\"leiqi\",\"longpeiqin\",\"longpeiqing\",\"liuyongren1\",\"zhouyaoyong\",\"test11\",\"liuli2\",\"huanghongfei\",\"nixianyin\",\"huangjianping\",\"xupeiwen\",\"zhuanghaojia\",\"xiaolizhu\",\"songguanye\",\"dingcun\",\"zhangmo\",\"wanggeng\",\"wangshuang\",\"sulin3\",\"tianxiong\",\"yangyizhuo\",\"panzhaochao\",\"lijiashi\",\"luzhou\",\"yanxianbiao\",\"dengruting\",\"xiehui\",\"wuzhejun\",\"yuanqingming\",\"zhengxuening\",\"test\",\"xuweicheng\",\"jianglinfeng\",\"test22\",\"test333\",\"jipeng\",\"wangzhi2\",\"lixiaoping\",\"testhpq\",\"zhangxudong\",\"linshishi\",\"ouyangyaxiong\",\"zengzheng\",\"gaojianwen\",\"test03\",\"test04\",\"test05\",\"xiongjingyang\",\"zhangbin\",\"hehuake\",\"huyong\",\"lianxiongshan\",\"liqiang\",\"zhongzhihua\",\"xujianping\",\"urctest2\",\"urctest0\",\"urctest1\",\"urctest3\",\"urctest4\",\"urctest5\",\"urctest6\",\"urctest7\",\"urctest8\",\"urctest9\",\"urctest10\",\"urctest11\",\"urctest12\",\"urctest13\",\"urctest14\",\"urctest15\",\"urctest16\",\"urctest17\",\"urctest18\",\"urctest19\",\"urctest20\",\"urctest21\",\"urctest22\",\"urctest23\",\"urctest24\",\"urctest25\",\"urctest26\",\"urctest27\",\"urctest28\",\"urctest29\",\"urctest30\",\"urctest31\",\"urctest32\",\"urctest33\",\"urctest34\",\"urctest35\",\"urctest36\",\"urctest37\",\"urctest38\",\"urctest39\",\"urctest40\",\"urctest41\",\"urctest42\",\"urctest43\",\"urctest44\",\"urctest45\",\"urctest46\",\"urctest47\",\"urctest48\",\"urctest49\",\"urctest50\",\"urctest51\",\"urctest52\",\"urctest53\",\"urctest54\",\"urctest55\",\"urctest56\",\"urctest57\",\"urctest58\",\"urctest59\",\"urctest60\",\"urctest61\",\"urctest62\",\"urctest63\",\"urctest64\",\"urctest65\",\"urctest66\",\"urctest67\",\"urctest68\",\"urctest69\",\"urctest70\",\"urctest71\",\"urctest72\",\"urctest73\",\"urctest74\",\"urctest75\",\"urctest76\",\"urctest77\",\"urctest78\",\"urctest79\",\"urctest80\",\"urctest81\",\"urctest82\",\"urctest83\",\"urctest84\",\"urctest85\",\"urctest86\",\"urctest87\",\"urctest88\",\"urctest89\",\"urctest90\",\"urctest91\",\"urctest92\",\"urctest93\",\"urctest94\",\"urctest95\",\"urctest96\",\"urctest97\",\"urctest98\",\"urctest99\",\"urctest100\",\"urctest101\",\"urctest102\",\"urctest103\",\"urctest104\",\"urctest105\",\"urctest106\",\"urctest107\",\"urctest108\",\"urctest109\",\"urctest110\",\"urctest111\",\"urctest112\",\"urctest113\",\"urctest114\",\"urctest115\",\"urctest116\",\"urctest117\",\"urctest118\",\"urctest119\",\"urctest120\",\"urctest121\",\"urctest122\",\"urctest123\",\"urctest124\",\"urctest125\",\"urctest126\",\"urctest127\",\"urctest128\",\"urctest129\",\"urctest130\",\"urctest131\",\"urctest132\",\"urctest133\",\"urctest134\",\"urctest135\",\"urctest136\",\"urctest137\",\"urctest138\",\"urctest139\",\"urctest140\",\"urctest141\",\"urctest142\",\"urctest143\",\"urctest144\",\"urctest145\",\"urctest146\",\"urctest147\",\"urctest148\",\"urctest149\",\"urctest150\",\"urctest151\",\"urctest152\",\"urctest153\",\"urctest154\",\"urctest155\",\"urctest156\",\"urctest157\",\"urctest158\",\"urctest159\",\"urctest160\",\"urctest161\",\"urctest162\",\"urctest163\",\"urctest164\",\"urctest165\",\"urctest166\",\"urctest167\",\"urctest168\",\"urctest169\",\"urctest170\",\"urctest171\",\"urctest172\",\"urctest173\",\"urctest174\",\"urctest175\",\"urctest176\",\"urctest177\",\"urctest178\",\"urctest179\",\"urctest180\",\"urctest181\",\"urctest182\",\"urctest183\",\"urctest184\",\"urctest185\",\"urctest186\",\"urctest187\",\"urctest188\",\"urctest189\",\"urctest190\",\"urctest191\",\"urctest192\",\"urctest193\",\"urctest194\",\"urctest195\",\"urctest196\",\"urctest197\",\"urctest198\",\"urctest199\",\"urctest200\",\"urctest201\",\"urctest202\",\"urctest203\",\"urctest204\",\"urctest205\",\"urctest206\",\"urctest207\",\"urctest208\",\"urctest209\",\"urctest210\",\"urctest211\",\"urctest212\",\"urctest213\",\"urctest214\",\"urctest215\",\"urctest216\",\"urctest217\",\"urctest218\",\"urctest219\",\"urctest220\",\"urctest221\",\"urctest222\",\"urctest223\",\"urctest224\",\"urctest225\",\"urctest226\",\"urctest227\",\"urctest228\",\"urctest229\",\"urctest230\",\"urctest231\",\"urctest232\",\"urctest233\",\"urctest234\",\"urctest235\",\"urctest236\",\"urctest237\",\"urctest238\",\"urctest239\",\"urctest240\",\"urctest241\",\"urctest242\",\"urctest243\",\"urctest244\",\"urctest245\",\"urctest246\",\"urctest247\",\"urctest248\",\"urctest249\",\"urctest250\",\"urctest251\",\"urctest252\",\"urctest253\",\"urctest254\",\"urctest255\",\"urctest256\",\"urctest257\",\"urctest258\",\"urctest259\",\"urctest260\",\"urctest261\",\"urctest262\",\"urctest263\",\"urctest264\",\"urctest265\",\"urctest266\",\"urctest267\",\"urctest268\",\"urctest269\",\"urctest270\",\"urctest271\",\"urctest272\",\"urctest273\",\"urctest274\",\"urctest275\",\"urctest276\",\"urctest277\",\"urctest278\",\"urctest279\",\"urctest280\",\"urctest281\",\"urctest282\",\"urctest283\",\"urctest284\",\"urctest285\",\"urctest286\",\"urctest287\",\"urctest288\",\"urctest289\",\"urctest290\",\"urctest291\",\"urctest292\",\"urctest293\",\"urctest294\",\"urctest295\",\"urctest296\",\"urctest297\",\"urctest298\",\"urctest299\",\"urctest300\",\"urctest301\",\"urctest302\",\"urctest303\",\"urctest304\",\"urctest305\",\"urctest306\",\"urctest307\",\"urctest308\",\"urctest309\",\"urctest310\",\"urctest311\",\"urctest312\",\"urctest313\",\"urctest314\",\"urctest315\",\"urctest316\",\"urctest317\",\"urctest318\",\"urctest319\",\"urctest320\",\"urctest321\",\"urctest322\",\"urctest323\",\"urctest324\",\"urctest325\",\"urctest326\",\"urctest327\",\"urctest328\",\"urctest329\",\"urctest330\",\"urctest331\",\"urctest332\",\"urctest333\",\"urctest334\",\"urctest335\",\"urctest336\",\"urctest337\",\"urctest338\",\"urctest339\",\"urctest340\",\"urctest341\",\"urctest342\",\"urctest343\",\"urctest344\",\"urctest345\",\"urctest346\",\"urctest347\",\"urctest348\",\"urctest349\",\"urctest350\",\"urctest351\",\"urctest352\",\"urctest353\",\"urctest354\",\"urctest355\",\"urctest356\",\"urctest357\",\"urctest358\",\"urctest359\",\"urctest360\",\"urctest361\",\"urctest362\",\"urctest363\",\"urctest364\",\"urctest365\",\"urctest366\",\"urctest367\",\"urctest368\",\"urctest369\",\"urctest370\",\"urctest371\",\"urctest372\",\"urctest373\",\"urctest374\",\"urctest375\",\"urctest376\",\"urctest377\",\"urctest378\",\"urctest379\",\"urctest380\",\"urctest381\",\"urctest382\",\"urctest383\",\"urctest384\",\"urctest385\",\"urctest386\",\"urctest387\",\"urctest388\",\"urctest389\",\"urctest390\",\"urctest391\",\"urctest392\",\"urctest393\",\"urctest394\",\"urctest395\",\"urctest396\",\"urctest397\",\"urctest398\",\"urctest399\",\"urctest400\",\"urctest401\",\"urctest402\",\"urctest403\",\"urctest404\",\"urctest405\",\"urctest406\",\"urctest407\",\"urctest408\",\"urctest409\",\"urctest410\",\"urctest411\",\"urctest412\",\"urctest413\",\"urctest414\",\"urctest415\",\"urctest416\",\"urctest417\",\"urctest418\",\"urctest419\",\"urctest420\",\"urctest421\",\"urctest422\",\"urctest423\",\"urctest424\",\"urctest425\",\"urctest426\",\"urctest427\",\"urctest428\",\"urctest429\",\"urctest430\",\"urctest431\",\"urctest432\",\"urctest433\",\"urctest434\",\"urctest435\",\"urctest436\",\"urctest437\",\"urctest438\",\"urctest439\",\"urctest440\",\"urctest441\",\"urctest442\",\"urctest443\",\"urctest444\",\"urctest445\",\"urctest446\",\"urctest447\",\"urctest448\",\"urctest449\",\"urctest450\",\"urctest451\",\"urctest452\",\"urctest453\",\"urctest454\",\"urctest455\",\"urctest456\",\"urctest457\",\"urctest458\",\"urctest459\",\"urctest460\",\"urctest461\",\"urctest462\",\"urctest463\",\"urctest464\",\"urctest465\",\"urctest466\",\"urctest467\",\"urctest468\",\"urctest469\",\"urctest470\",\"urctest471\",\"urctest472\",\"urctest473\",\"urctest474\",\"urctest475\",\"urctest476\",\"urctest477\",\"urctest478\",\"urctest479\",\"urctest480\",\"urctest481\",\"urctest482\",\"urctest483\",\"urctest484\",\"urctest485\",\"urctest486\",\"urctest487\",\"urctest488\",\"urctest489\",\"urctest490\",\"urctest491\",\"urctest492\",\"urctest493\",\"urctest494\",\"urctest495\",\"urctest496\",\"urctest497\",\"urctest498\",\"urctest499\",\"urctest500\",\"urctest501\",\"urctest502\",\"urctest503\",\"urctest504\",\"urctest505\",\"urctest506\",\"urctest507\",\"urctest508\",\"urctest509\",\"urctest510\",\"urctest511\",\"urctest512\",\"urctest513\",\"urctest514\",\"urctest515\",\"urctest516\",\"urctest517\",\"urctest518\",\"urctest519\",\"urctest520\",\"urctest521\",\"urctest522\",\"urctest523\",\"urctest524\",\"urctest525\",\"urctest526\",\"urctest527\",\"urctest528\",\"urctest529\",\"urctest530\",\"urctest531\",\"urctest532\",\"urctest533\",\"urctest534\",\"urctest535\",\"urctest536\",\"urctest537\",\"urctest538\",\"urctest539\",\"urctest540\",\"urctest541\",\"urctest542\",\"urctest543\",\"urctest544\",\"urctest545\",\"urctest546\",\"urctest547\",\"urctest548\",\"urctest549\",\"urctest550\",\"urctest551\",\"urctest552\",\"urctest553\",\"urctest554\",\"urctest555\",\"urctest556\",\"urctest557\",\"urctest558\",\"urctest559\",\"urctest560\",\"urctest561\",\"urctest562\",\"urctest563\",\"urctest564\",\"urctest565\",\"urctest566\",\"urctest567\",\"urctest568\",\"urctest569\",\"urctest570\",\"urctest571\",\"urctest572\",\"urctest573\",\"urctest574\",\"urctest575\",\"urctest576\",\"urctest577\",\"urctest578\",\"urctest579\",\"urctest580\",\"urctest581\",\"urctest582\",\"urctest583\",\"urctest584\",\"urctest585\",\"urctest586\",\"urctest587\",\"urctest588\",\"urctest589\",\"urctest590\",\"urctest591\",\"urctest592\",\"urctest593\",\"urctest594\",\"urctest595\",\"urctest596\",\"urctest597\",\"urctest598\",\"urctest599\",\"urctest600\",\"urctest601\",\"urctest602\",\"urctest603\",\"urctest604\",\"urctest605\",\"urctest606\",\"urctest607\",\"urctest608\",\"urctest609\",\"urctest610\",\"urctest611\",\"urctest612\",\"urctest613\",\"urctest614\",\"urctest615\",\"urctest616\",\"urctest617\",\"urctest618\",\"urctest619\",\"urctest620\",\"urctest621\",\"urctest622\",\"urctest623\",\"urctest624\",\"urctest625\",\"urctest626\",\"urctest627\",\"urctest628\",\"urctest629\",\"urctest630\",\"urctest631\",\"urctest632\",\"urctest633\",\"urctest634\",\"urctest635\",\"urctest636\",\"urctest637\",\"urctest638\",\"urctest639\",\"urctest640\",\"urctest641\",\"urctest642\",\"urctest643\",\"urctest644\",\"urctest645\",\"urctest646\",\"urctest647\",\"urctest648\",\"urctest649\",\"urctest650\",\"urctest651\",\"urctest652\",\"urctest653\",\"urctest654\",\"urctest655\",\"urctest656\",\"urctest657\",\"urctest658\",\"urctest659\",\"urctest660\",\"urctest661\",\"urctest662\",\"urctest663\",\"urctest664\",\"urctest665\",\"urctest666\",\"urctest667\",\"urctest668\",\"urctest669\",\"urctest670\",\"urctest671\",\"urctest672\",\"urctest673\",\"urctest674\",\"urctest675\",\"urctest676\",\"urctest677\",\"urctest678\",\"urctest679\",\"urctest680\",\"urctest681\",\"urctest682\",\"urctest683\",\"urctest684\",\"urctest685\",\"urctest686\",\"urctest687\",\"urctest688\",\"urctest689\",\"urctest690\",\"urctest691\",\"urctest692\",\"urctest693\",\"urctest694\",\"urctest695\",\"urctest696\",\"urctest697\",\"urctest698\",\"urctest699\",\"urctest700\",\"urctest701\",\"urctest702\",\"urctest703\",\"urctest704\",\"urctest705\",\"urctest706\",\"urctest707\",\"urctest708\",\"urctest709\",\"urctest710\",\"urctest711\",\"urctest712\",\"urctest713\",\"urctest714\",\"urctest715\",\"urctest716\",\"urctest717\",\"urctest718\",\"urctest719\",\"urctest720\",\"urctest721\",\"urctest722\",\"urctest723\",\"urctest724\",\"urctest725\",\"urctest726\",\"urctest727\",\"urctest728\",\"urctest729\",\"urctest730\",\"urctest731\",\"urctest732\",\"urctest733\",\"urctest734\",\"urctest735\",\"urctest736\",\"urctest737\",\"urctest738\",\"urctest739\",\"urctest740\",\"urctest741\",\"urctest742\",\"urctest743\",\"urctest744\",\"urctest745\",\"urctest746\",\"urctest747\",\"urctest748\",\"urctest749\",\"urctest750\",\"urctest751\",\"urctest752\",\"urctest753\",\"urctest754\",\"urctest755\",\"urctest756\",\"urctest757\",\"urctest758\",\"urctest759\",\"urctest760\",\"urctest761\",\"urctest762\",\"urctest763\",\"urctest764\",\"urctest765\",\"urctest766\",\"urctest767\",\"urctest768\",\"urctest769\",\"urctest770\",\"urctest771\",\"urctest772\",\"urctest773\",\"urctest774\",\"urctest775\",\"urctest776\",\"urctest777\",\"urctest778\",\"urctest779\",\"urctest780\",\"urctest781\",\"urctest782\",\"urctest783\",\"urctest784\",\"urctest785\",\"urctest786\",\"urctest787\",\"urctest788\",\"urctest789\",\"urctest790\",\"urctest791\",\"urctest792\",\"urctest793\",\"urctest794\",\"urctest795\",\"urctest796\",\"urctest797\",\"urctest798\",\"urctest799\",\"urctest800\",\"urctest801\",\"urctest802\",\"urctest803\",\"urctest804\",\"urctest805\",\"urctest806\",\"urctest807\",\"urctest808\",\"urctest809\",\"urctest810\",\"urctest811\",\"urctest812\",\"urctest813\",\"urctest814\",\"urctest815\",\"urctest816\",\"urctest817\",\"urctest818\",\"urctest819\",\"urctest820\",\"urctest821\",\"urctest822\",\"urctest823\",\"urctest824\",\"urctest825\",\"urctest826\",\"urctest827\",\"urctest828\",\"urctest829\",\"urctest830\",\"urctest831\",\"urctest832\",\"urctest833\",\"urctest834\",\"urctest835\",\"urctest836\",\"urctest837\",\"urctest838\",\"urctest839\",\"urctest840\",\"urctest841\",\"urctest842\",\"urctest843\",\"urctest844\",\"urctest845\",\"urctest846\",\"urctest847\",\"urctest848\",\"urctest849\",\"urctest850\",\"urctest851\",\"urctest852\",\"urctest853\",\"urctest854\",\"urctest855\",\"urctest856\",\"urctest857\",\"urctest858\",\"urctest859\",\"urctest860\",\"urctest861\",\"urctest862\",\"urctest863\",\"urctest864\",\"urctest865\",\"urctest866\",\"urctest867\",\"urctest868\",\"urctest869\",\"urctest870\",\"urctest871\",\"urctest872\",\"urctest873\",\"urctest874\",\"urctest875\",\"urctest876\",\"urctest877\",\"urctest878\",\"urctest879\",\"urctest880\",\"urctest881\",\"urctest882\",\"urctest883\",\"urctest884\",\"urctest885\",\"urctest886\",\"urctest887\",\"urctest888\",\"urctest889\",\"urctest890\",\"urctest891\",\"urctest892\",\"urctest893\",\"urctest894\",\"urctest895\",\"urctest896\",\"urctest897\",\"urctest898\",\"urctest899\",\"urctest900\",\"urctest901\",\"urctest902\",\"urctest903\",\"urctest904\",\"urctest905\",\"urctest906\",\"urctest907\",\"urctest908\",\"urctest909\",\"urctest910\",\"urctest911\",\"urctest912\",\"urctest913\",\"urctest914\",\"urctest915\",\"urctest916\",\"urctest917\",\"urctest918\",\"urctest919\",\"urctest920\",\"urctest921\",\"urctest922\",\"urctest923\",\"urctest924\",\"urctest925\",\"urctest926\",\"urctest927\",\"urctest928\",\"urctest929\",\"urctest930\",\"urctest931\",\"urctest932\",\"urctest933\",\"urctest934\",\"urctest935\",\"urctest936\",\"urctest937\",\"urctest938\",\"urctest939\",\"urctest940\",\"urctest941\",\"urctest942\",\"urctest943\",\"urctest944\",\"urctest945\",\"urctest946\",\"urctest947\",\"urctest948\",\"urctest949\",\"urctest950\",\"urctest951\",\"urctest952\",\"urctest953\",\"urctest954\",\"urctest955\",\"urctest956\",\"urctest957\",\"urctest958\",\"urctest959\",\"urctest960\",\"urctest961\",\"urctest962\",\"urctest963\",\"urctest964\",\"urctest965\",\"urctest966\",\"urctest967\",\"urctest968\",\"urctest969\",\"urctest970\",\"urctest971\",\"urctest972\",\"urctest973\",\"urctest974\",\"urctest975\",\"urctest976\",\"urctest977\",\"urctest978\",\"urctest979\",\"urctest980\",\"urctest981\",\"urctest982\",\"urctest983\",\"urctest984\",\"urctest985\",\"urctest986\",\"urctest987\",\"urctest988\",\"urctest989\",\"urctest990\",\"urctest991\",\"urctest992\",\"urctest993\",\"urctest994\",\"urctest995\",\"urctest996\",\"urctest997\",\"urctest998\",\"urctest999\",\"urctest1000\",\"urctest1001\",\"urctest1002\",\"urctest1003\",\"urctest1004\",\"urctest1005\",\"urctest1006\",\"urctest1007\",\"urctest1008\",\"urctest1009\",\"urctest1010\",\"urctest1011\",\"urctest1012\",\"urctest1013\",\"urctest1014\",\"urctest1015\",\"urctest1016\",\"urctest1017\",\"urctest1018\",\"urctest1019\",\"urctest1020\",\"urctest1021\",\"urctest1022\",\"urctest1023\",\"urctest1024\",\"urctest1025\",\"urctest1026\",\"urctest1027\",\"urctest1028\",\"urctest1029\",\"urctest1030\",\"urctest1031\",\"urctest1032\",\"urctest1033\",\"urctest1034\",\"urctest1035\",\"urctest1036\",\"urctest1037\",\"urctest1038\",\"urctest1039\",\"urctest1040\",\"urctest1041\",\"urctest1042\",\"urctest1043\",\"urctest1044\",\"urctest1045\",\"urctest1046\",\"urctest1047\",\"urctest1048\",\"urctest1049\",\"urctest1050\",\"urctest1051\",\"urctest1052\",\"urctest1053\",\"urctest1054\",\"urctest1055\",\"urctest1056\",\"urctest1057\",\"urctest1058\",\"urctest1059\",\"urctest1060\",\"urctest1061\",\"urctest1062\",\"urctest1063\",\"urctest1064\",\"urctest1065\",\"urctest1066\",\"urctest1067\",\"urctest1068\",\"urctest1069\",\"urctest1070\",\"urctest1071\",\"urctest1072\",\"urctest1073\",\"urctest1074\",\"urctest1075\",\"urctest1076\",\"urctest1077\",\"urctest1078\",\"urctest1079\",\"urctest1080\",\"urctest1081\",\"urctest1082\",\"urctest1083\",\"urctest1084\",\"urctest1085\",\"urctest1086\",\"urctest1087\",\"urctest1088\",\"urctest1089\",\"urctest1090\",\"urctest1091\",\"urctest1092\",\"urctest1093\",\"urctest1094\",\"urctest1095\",\"urctest1096\",\"urctest1097\",\"urctest1098\",\"urctest1099\",\"urctest1100\",\"urctest1101\",\"urctest1102\",\"urctest1103\",\"urctest1104\",\"urctest1105\",\"urctest1106\",\"urctest1107\",\"urctest1108\",\"urctest1109\",\"urctest1110\",\"urctest1111\",\"urctest1112\",\"urctest1113\",\"urctest1114\",\"urctest1115\",\"urctest1116\",\"urctest1117\",\"urctest1118\",\"urctest1119\",\"urctest1120\",\"urctest1121\",\"urctest1122\",\"urctest1123\",\"urctest1124\",\"urctest1125\",\"urctest1126\",\"urctest1127\",\"urctest1128\",\"urctest1129\",\"urctest1130\",\"urctest1131\",\"urctest1132\",\"urctest1133\",\"urctest1134\",\"urctest1135\",\"urctest1136\",\"urctest1137\",\"urctest1138\",\"urctest1139\",\"urctest1140\",\"urctest1141\",\"urctest1142\",\"urctest1143\",\"urctest1144\",\"urctest1145\",\"urctest1146\",\"urctest1147\",\"urctest1148\",\"urctest1149\",\"urctest1150\",\"urctest1151\",\"urctest1152\",\"urctest1153\",\"urctest1154\",\"urctest1155\",\"urctest1156\",\"urctest1157\",\"urctest1158\",\"urctest1159\",\"urctest1160\",\"urctest1161\",\"urctest1162\",\"urctest1163\",\"urctest1164\",\"urctest1165\",\"urctest1166\",\"urctest1167\",\"urctest1168\",\"urctest1169\",\"urctest1170\",\"urctest1171\",\"urctest1172\",\"urctest1173\",\"urctest1174\",\"urctest1175\",\"urctest1176\",\"urctest1177\",\"urctest1178\",\"urctest1179\",\"urctest1180\",\"urctest1181\",\"urctest1182\",\"urctest1183\",\"urctest1184\",\"urctest1185\",\"urctest1186\",\"urctest1187\",\"urctest1188\",\"urctest1189\",\"urctest1190\",\"urctest1191\",\"urctest1192\",\"urctest1193\",\"urctest1194\",\"urctest1195\",\"urctest1196\",\"urctest1197\",\"urctest1198\",\"urctest1199\",\"urctest1200\",\"urctest1201\",\"urctest1202\",\"urctest1203\",\"urctest1204\",\"urctest1205\",\"urctest1206\",\"urctest1207\",\"urctest1208\",\"urctest1209\",\"urctest1210\",\"urctest1211\",\"urctest1212\",\"urctest1213\",\"urctest1214\",\"urctest1215\",\"urctest1216\",\"urctest1217\",\"urctest1218\",\"urctest1219\",\"urctest1220\",\"urctest1221\",\"urctest1222\",\"urctest1223\",\"urctest1224\",\"urctest1225\",\"urctest1226\",\"urctest1227\",\"urctest1228\",\"urctest1229\",\"urctest1230\",\"urctest1231\",\"urctest1232\",\"urctest1233\",\"urctest1234\",\"urctest1235\",\"urctest1236\",\"urctest1237\",\"urctest1238\",\"urctest1239\",\"urctest1240\",\"urctest1241\",\"urctest1242\",\"urctest1243\",\"urctest1244\",\"urctest1245\",\"urctest1246\",\"urctest1247\",\"urctest1248\",\"urctest1249\",\"urctest1250\",\"urctest1251\",\"urctest1252\",\"urctest1253\",\"urctest1254\",\"urctest1255\",\"urctest1256\",\"urctest1257\",\"urctest1258\",\"urctest1259\",\"urctest1260\",\"urctest1261\",\"urctest1262\",\"urctest1263\",\"urctest1264\",\"urctest1265\",\"urctest1266\",\"urctest1267\",\"urctest1268\",\"urctest1269\",\"urctest1270\",\"urctest1271\",\"urctest1272\",\"urctest1273\",\"urctest1274\",\"urctest1275\",\"urctest1276\",\"urctest1277\",\"urctest1278\",\"urctest1279\",\"urctest1280\",\"urctest1281\",\"urctest1282\",\"urctest1283\",\"urctest1284\",\"urctest1285\",\"urctest1286\",\"urctest1287\",\"urctest1288\",\"urctest1289\",\"urctest1290\",\"urctest1291\",\"urctest1292\",\"urctest1293\",\"urctest1294\",\"urctest1295\",\"urctest1296\",\"urctest1297\",\"urctest1298\",\"urctest1299\",\"urctest1300\",\"urctest1301\",\"urctest1302\",\"urctest1303\",\"urctest1304\",\"urctest1305\",\"urctest1306\",\"urctest1307\",\"urctest1308\",\"urctest1309\",\"urctest1310\",\"urctest1311\",\"urctest1312\",\"urctest1313\",\"urctest1314\",\"urctest1315\",\"urctest1316\",\"urctest1317\",\"urctest1318\",\"urctest1319\",\"urctest1320\",\"urctest1321\",\"urctest1322\",\"urctest1323\",\"urctest1324\",\"urctest1325\",\"urctest1326\",\"urctest1327\",\"urctest1328\",\"urctest1329\",\"urctest1330\",\"urctest1331\",\"urctest1332\",\"urctest1333\",\"urctest1334\",\"urctest1335\",\"urctest1336\",\"urctest1337\",\"urctest1338\",\"urctest1339\",\"urctest1340\",\"urctest1341\",\"urctest1342\",\"urctest1343\",\"urctest1344\",\"urctest1345\",\"urctest1346\",\"urctest1347\",\"urctest1348\",\"urctest1349\",\"urctest1350\",\"urctest1351\",\"urctest1352\",\"urctest1353\",\"urctest1354\",\"urctest1355\",\"urctest1356\",\"urctest1357\",\"urctest1358\",\"urctest1359\",\"urctest1360\",\"urctest1361\",\"urctest1362\",\"urctest1363\",\"urctest1364\",\"urctest1365\",\"urctest1366\",\"urctest1367\",\"urctest1368\",\"urctest1369\",\"urctest1370\",\"urctest1371\",\"urctest1372\",\"urctest1373\",\"urctest1374\",\"urctest1375\",\"urctest1376\",\"urctest1377\",\"urctest1378\",\"urctest1379\",\"urctest1380\",\"urctest1381\",\"urctest1382\",\"urctest1383\",\"urctest1384\",\"urctest1385\",\"urctest1386\",\"urctest1387\",\"urctest1388\",\"urctest1389\",\"urctest1390\",\"urctest1391\",\"urctest1392\",\"urctest1393\",\"urctest1394\",\"urctest1395\",\"urctest1396\",\"urctest1397\",\"urctest1398\",\"urctest1399\",\"urctest1400\",\"urctest1401\",\"urctest1402\",\"urctest1403\",\"urctest1404\",\"urctest1405\",\"urctest1406\",\"urctest1407\",\"urctest1408\",\"urctest1409\",\"urctest1410\",\"urctest1411\",\"urctest1412\",\"urctest1413\",\"urctest1414\",\"urctest1415\",\"urctest1416\",\"urctest1417\",\"urctest1418\",\"urctest1419\",\"urctest1420\",\"urctest1421\",\"urctest1422\",\"urctest1423\",\"urctest1424\",\"urctest1425\",\"urctest1426\",\"urctest1427\",\"urctest1428\",\"urctest1429\",\"urctest1430\",\"urctest1431\",\"urctest1432\",\"urctest1433\",\"urctest1434\",\"urctest1435\",\"urctest1436\",\"urctest1437\",\"urctest1438\",\"urctest1439\",\"urctest1440\",\"urctest1441\",\"urctest1442\",\"urctest1443\",\"urctest1444\",\"urctest1445\",\"urctest1446\",\"urctest1447\",\"urctest1448\",\"urctest1449\",\"urctest1450\",\"urctest1451\",\"urctest1452\",\"urctest1453\",\"urctest1454\",\"urctest1455\",\"urctest1456\",\"urctest1457\",\"urctest1458\",\"urctest1459\",\"urctest1460\",\"urctest1461\",\"urctest1462\",\"urctest1463\",\"urctest1464\",\"urctest1465\",\"urctest1466\",\"urctest1467\",\"urctest1468\",\"urctest1469\",\"urctest1470\",\"urctest1471\",\"urctest1472\",\"urctest1473\",\"urctest1474\",\"urctest1475\",\"urctest1476\",\"urctest1477\",\"urctest1478\",\"urctest1479\",\"urctest1480\",\"urctest1481\",\"urctest1482\",\"urctest1483\",\"urctest1484\",\"urctest1485\",\"urctest1486\",\"urctest1487\",\"urctest1488\",\"urctest1489\",\"urctest1490\",\"urctest1491\",\"urctest1492\",\"urctest1493\",\"urctest1494\",\"urctest1495\",\"urctest1496\",\"urctest1497\",\"urctest1498\",\"urctest1499\",\"urctest1500\",\"urctest1501\",\"urctest1502\",\"urctest1503\",\"urctest1504\",\"urctest1505\",\"urctest1506\",\"urctest1507\",\"urctest1508\",\"urctest1509\",\"urctest1510\",\"urctest1511\",\"urctest1512\",\"urctest1513\",\"urctest1514\",\"urctest1515\",\"urctest1516\",\"urctest1517\",\"urctest1518\",\"urctest1519\",\"urctest1520\",\"urctest1521\",\"urctest1522\",\"urctest1523\",\"urctest1524\",\"urctest1525\",\"urctest1526\",\"urctest1527\",\"urctest1528\",\"urctest1529\",\"urctest1530\",\"urctest1531\",\"urctest1532\",\"urctest1533\",\"urctest1534\",\"urctest1535\",\"urctest1536\",\"urctest1537\",\"urctest1538\",\"urctest1539\",\"urctest1540\",\"urctest1541\",\"urctest1542\",\"urctest1543\",\"urctest1544\",\"urctest1545\",\"urctest1546\",\"urctest1547\",\"urctest1548\",\"urctest1549\",\"urctest1550\",\"urctest1551\",\"urctest1552\",\"urctest1553\",\"urctest1554\",\"urctest1555\",\"urctest1556\",\"urctest1557\",\"urctest1558\",\"urctest1559\",\"urctest1560\",\"urctest1561\",\"urctest1562\",\"urctest1563\",\"urctest1564\",\"urctest1565\",\"urctest1566\",\"urctest1567\",\"urctest1568\",\"urctest1569\",\"urctest1570\",\"urctest1571\",\"urctest1572\",\"urctest1573\",\"urctest1574\",\"urctest1575\",\"urctest1576\",\"urctest1577\",\"urctest1578\",\"urctest1579\",\"urctest1580\",\"urctest1581\",\"urctest1582\",\"urctest1583\",\"urctest1584\",\"urctest1585\",\"urctest1586\",\"urctest1587\",\"urctest1588\",\"urctest1589\",\"urctest1590\",\"urctest1591\",\"urctest1592\",\"urctest1593\",\"urctest1594\",\"urctest1595\",\"urctest1596\",\"urctest1597\",\"urctest1598\",\"urctest1599\",\"urctest1600\",\"urctest1601\",\"urctest1602\",\"urctest1603\",\"urctest1604\",\"urctest1605\",\"urctest1606\",\"urctest1607\",\"urctest1608\",\"urctest1609\",\"urctest1610\",\"urctest1611\",\"urctest1612\",\"urctest1613\",\"urctest1614\",\"urctest1615\",\"urctest1616\",\"urctest1617\",\"urctest1618\",\"urctest1619\",\"urctest1620\",\"urctest1621\",\"urctest1622\",\"urctest1623\",\"urctest1624\",\"urctest1625\",\"urctest1626\",\"urctest1627\",\"urctest1628\",\"urctest1629\",\"urctest1630\",\"urctest1631\",\"urctest1632\",\"urctest1633\",\"urctest1634\",\"urctest1635\",\"urctest1636\",\"urctest1637\",\"urctest1638\",\"urctest1639\",\"urctest1640\",\"urctest1641\",\"urctest1642\",\"urctest1643\",\"urctest1644\",\"urctest1645\",\"urctest1646\",\"urctest1647\",\"urctest1648\",\"urctest1649\",\"urctest1650\",\"urctest1651\",\"urctest1652\",\"urctest1653\",\"urctest1654\",\"urctest1655\",\"urctest1656\",\"urctest1657\",\"urctest1658\",\"urctest1659\",\"urctest1660\",\"urctest1661\",\"urctest1662\",\"urctest1663\",\"urctest1664\",\"urctest1665\",\"urctest1666\",\"urctest1667\",\"urctest1668\",\"urctest1669\",\"urctest1670\",\"urctest1671\",\"urctest1672\",\"urctest1673\",\"urctest1674\",\"urctest1675\",\"urctest1676\",\"urctest1677\",\"urctest1678\",\"urctest1679\",\"urctest1680\",\"urctest1681\",\"urctest1682\",\"urctest1683\",\"urctest1684\",\"urctest1685\",\"urctest1686\",\"urctest1687\",\"urctest1688\",\"urctest1689\",\"urctest1690\",\"urctest1691\",\"urctest1692\",\"urctest1693\",\"urctest1694\",\"urctest1695\",\"urctest1696\",\"urctest1697\",\"urctest1698\",\"urctest1699\",\"urctest1700\",\"urctest1701\",\"urctest1702\",\"urctest1703\",\"urctest1704\",\"urctest1705\",\"urctest1706\",\"urctest1707\",\"urctest1708\",\"urctest1709\",\"urctest1710\",\"urctest1711\",\"urctest1712\",\"urctest1713\",\"urctest1714\",\"urctest1715\",\"urctest1716\",\"urctest1717\",\"urctest1718\",\"urctest1719\",\"urctest1720\",\"urctest1721\",\"urctest1722\",\"urctest1723\",\"urctest1724\",\"urctest1725\",\"urctest1726\",\"urctest1727\",\"urctest1728\",\"urctest1729\",\"urctest1730\",\"urctest1731\",\"urctest1732\",\"urctest1733\",\"urctest1734\",\"urctest1735\",\"urctest1736\",\"urctest1737\",\"urctest1738\",\"urctest1739\",\"urctest1740\",\"urctest1741\",\"urctest1742\",\"urctest1743\",\"urctest1744\",\"urctest1745\",\"urctest1746\",\"urctest1747\",\"urctest1748\",\"urctest1749\",\"urctest1750\",\"urctest1751\",\"urctest1752\",\"urctest1753\",\"urctest1754\",\"urctest1755\",\"urctest1756\",\"urctest1757\",\"urctest1758\",\"urctest1759\",\"urctest1760\",\"urctest1761\",\"urctest1762\",\"urctest1763\",\"urctest1764\",\"urctest1765\",\"urctest1766\",\"urctest1767\",\"urctest1768\",\"urctest1769\",\"urctest1770\",\"urctest1771\",\"urctest1772\",\"urctest1773\",\"urctest1774\",\"urctest1775\",\"urctest1776\",\"urctest1777\",\"urctest1778\",\"urctest1779\",\"urctest1780\",\"urctest1781\",\"urctest1782\",\"urctest1783\",\"urctest1784\",\"urctest1785\",\"urctest1786\",\"urctest1787\",\"urctest1788\",\"urctest1789\",\"urctest1790\",\"urctest1791\",\"urctest1792\",\"urctest1793\",\"urctest1794\",\"urctest1795\",\"urctest1796\",\"urctest1797\",\"urctest1798\",\"urctest1799\",\"urctest1800\",\"urctest1801\",\"urctest1802\",\"urctest1803\",\"urctest1804\",\"urctest1805\",\"urctest1806\",\"urctest1807\",\"urctest1808\",\"urctest1809\",\"urctest1810\",\"urctest1811\",\"urctest1812\",\"urctest1813\",\"urctest1814\",\"urctest1815\",\"urctest1816\",\"urctest1817\",\"urctest1818\",\"urctest1819\",\"urctest1820\",\"urctest1821\",\"urctest1822\",\"urctest1823\",\"urctest1824\",\"urctest1825\",\"urctest1826\",\"urctest1827\",\"urctest1828\",\"urctest1829\",\"urctest1830\",\"urctest1831\",\"urctest1832\",\"urctest1833\",\"urctest1834\",\"urctest1835\",\"urctest1836\",\"urctest1837\",\"urctest1838\",\"urctest1839\",\"urctest1840\",\"urctest1841\",\"urctest1842\",\"urctest1843\",\"urctest1844\",\"urctest1845\",\"urctest1846\",\"urctest1847\",\"urctest1848\",\"urctest1849\",\"urctest1850\",\"urctest1851\",\"urctest1852\",\"urctest1853\",\"urctest1854\",\"urctest1855\",\"urctest1856\",\"urctest1857\",\"urctest1858\",\"urctest1859\",\"urctest1860\",\"urctest1861\",\"urctest1862\",\"urctest1863\",\"urctest1864\",\"urctest1865\",\"urctest1866\",\"urctest1867\",\"urctest1868\",\"urctest1869\",\"urctest1870\",\"urctest1871\",\"urctest1872\",\"urctest1873\",\"urctest1874\",\"urctest1875\",\"urctest1876\",\"urctest1877\",\"urctest1878\",\"urctest1879\",\"urctest1880\",\"urctest1881\",\"urctest1882\",\"urctest1883\",\"urctest1884\",\"urctest1885\",\"urctest1886\",\"urctest1887\",\"urctest1888\",\"urctest1889\",\"urctest1890\",\"urctest1891\",\"urctest1892\",\"urctest1893\",\"urctest1894\",\"urctest1895\",\"urctest1896\",\"urctest1897\",\"urctest1898\",\"urctest1899\",\"urctest1900\",\"urctest1901\",\"urctest1902\",\"urctest1903\",\"urctest1904\",\"urctest1905\",\"urctest1906\",\"urctest1907\",\"urctest1908\",\"urctest1909\",\"urctest1910\",\"urctest1911\",\"urctest1912\",\"urctest1913\",\"urctest1914\",\"urctest1915\",\"urctest1916\",\"urctest1917\",\"urctest1918\",\"urctest1919\",\"urctest1920\",\"urctest1921\",\"urctest1922\",\"urctest1923\",\"urctest1924\",\"urctest1925\",\"urctest1926\",\"urctest1927\",\"urctest1928\",\"urctest1929\",\"urctest1930\",\"urctest1931\",\"urctest1932\",\"urctest1933\",\"urctest1934\",\"urctest1935\",\"urctest1936\",\"urctest1937\",\"urctest1938\",\"urctest1939\",\"urctest1940\",\"urctest1941\",\"urctest1942\",\"urctest1943\",\"urctest1944\",\"urctest1945\",\"urctest1946\",\"urctest1947\",\"urctest1948\",\"urctest1949\",\"urctest1950\",\"urctest1951\",\"urctest1952\",\"urctest1953\",\"urctest1954\",\"urctest1955\",\"urctest1956\",\"urctest1957\",\"urctest1958\",\"urctest1959\",\"urctest1960\",\"urctest1961\",\"urctest1962\",\"urctest1963\",\"urctest1964\",\"urctest1965\",\"urctest1966\",\"urctest1967\",\"urctest1968\",\"urctest1969\",\"urctest1970\",\"urctest1971\",\"urctest1972\",\"urctest1973\",\"urctest1974\",\"urctest1975\",\"urctest1976\",\"urctest1977\",\"urctest1978\",\"urctest1979\",\"urctest1980\",\"urctest1981\",\"urctest1982\",\"urctest1983\",\"urctest1984\",\"urctest1985\",\"urctest1986\",\"urctest1987\",\"urctest1988\",\"urctest1989\",\"urctest1990\",\"urctest1991\",\"urctest1992\",\"urctest1993\",\"urctest1994\",\"urctest1995\",\"urctest1996\",\"urctest1997\",\"urctest1998\",\"urctest1999\",\"urctest2000\",\"huangxiaofang\",\"xuxiangyang\",\"lwx\",\"liguozheng\",\"fanyanhua\",\"wangyuan\",\"guochaoqun\",\"chengzaolin\",\"maofei\",\"gongxiaohua\",\"gongxiaofang\",\"guoqunchao\",\"yanxiaodan\",\"huangkai\",\"test01\",\"liushasha\",\"hpq01\",\"hpq02\",\"test001\",\"test002\",\"test003\",\"test004\",\"test005\",\"test006\",\"houyunfeng1\",\"test007\",\"test008\",\"test009\",\"test010\",\"shentao\",\"jiangshilin\",\"yaopeng\",\"lizhaohui\"],\"lstOwner\":[\"yangbo\"]},\"ticket\":\"c0cd52b539d8caeb404a834d5e3dd25f\",\"operator\":\"songguanye\",\"personName\":\"songguanye\",\"funcVersion\":\"dcd1d487cadebc841effd441e6fd9627\",\"moduleUrl\":\"/user/rolemanagement/addupdaterole/\"}";
        MotanSession.initialSession(json);
        Long startTime = new Date().getTime();
        resultVO = service.addOrUpdateRoleInfo(json);
        Long endTime = new Date().getTime();
        System.out.println("总花费时间"+ (endTime - startTime));
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
