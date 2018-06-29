/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/9
 * @since 1.0.0
 */
package com.yks.urc.service;

import com.alibaba.fastjson.JSONObject;
import com.yks.distributed.cache.core.Cache;
import com.yks.distributed.cache.core.DistributedCacheBuilder;
import com.yks.mq.client.MQConsumerClient;
import com.yks.mq.client.MQConsumerClient.MessageCallBack;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.dingding.client.DingApiProxy;
import com.yks.urc.entity.*;
import com.yks.urc.fw.EncryptHelper;
import com.yks.urc.fw.HttpUtility;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IRolePermissionMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.motan.service.impl.UrcServiceImpl;
import com.yks.urc.mq.bp.api.IMqBp;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.seq.bp.api.ISeqBp;
import com.yks.urc.service.api.IPermissionService;
import com.yks.urc.service.api.IUserService;
import com.yks.urc.user.bp.api.IUserBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.DataRuleSysVO;
import com.yks.urc.vo.DataRuleVO;
import com.yks.urc.vo.ExpressionVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.RoleVO;
import com.yks.urc.vo.UserVO;

import java.io.IOException;
import java.util.*;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

//@Component
public class PanYunUrcServiceTest extends BaseServiceTest {
    @Autowired
    private IUrcService service;
    @Autowired
    private IUserValidateBp userValidateBp;
    @Autowired
    private IUserBp userBp;
    @Autowired
    private ISeqBp seqBp;

    @Autowired
    IRoleMapper roleMapper;

    @Autowired
    private ICacheBp cacheBp;

    @Autowired
    private IPermitStatBp permitStatBp;

    @Autowired
    private IUserService userService;
    @Autowired
    IPermissionService permissionService;

    @Autowired
    IMqBp mqBp;

    @Value("${importSysPermit.aesPwd}")
    private String aesPwd;

    public void testIPermissionService() throws Exception {
        String strJson1 = StringUtility.inputStream2String(ClassLoader.getSystemResourceAsStream("oms.json"));
        String strEncrypt = EncryptHelper.encryptAes_Base64(strJson1, aesPwd);
        System.out.println(permissionService.importSysPermit(strEncrypt));
    }

    public static void main(String[] args) {
        String topic = String.format("URC_USER_DATARULE_%s", "001");
        MessageCallBack callBack = new MessageCallBack() {

            @Override
            public void call(String arg0, String arg1) {
                System.out.println(String.format("-----------------MessageCallBack:%s %s", arg0, arg1));
            }
        };
        new MQConsumerClient().subscribe(topic, callBack);
    }

    @Test
    public void mq_Test() {
        DataRuleVO dr = new DataRuleVO();
        dr.userName = "py";
        dr.lstDataRuleSys = new ArrayList<>();
        DataRuleSysVO e = new DataRuleSysVO();
        e.sysKey = "001";
        e.userName = "panyun";

        ExpressionVO exp = new ExpressionVO();
        exp.setIsAnd(1);
        List<ExpressionVO> subWhereClause = new ArrayList<>();
        ExpressionVO subExp = new ExpressionVO();
        subExp.setFiledCode("F_Platform_Shop_Site");
        subExp.setEntityCode("E_PlatformShopSite");
        subExp.setOper("in");
        List<String> operValuesArr = new ArrayList<>();
        subExp.setOperValuesArr(operValuesArr);
        subWhereClause.add(subExp);
        exp.setSubWhereClause(subWhereClause);
        e.row = exp;

        dr.lstDataRuleSys.add(e);
        mqBp.send2Mq(dr);
    }

    public void testUpdateExpiredRole() {
        List<RoleDO> lstRole = roleMapper.updateAllExpiredRole();
        System.out.println(StringUtility.toJSONString_NoException(lstRole));
    }

    public void testGetAllFuncPermit() {
        System.out.println(StringUtility.toJSONString_NoException(userBp.getAllFuncPermit("panyun")));
    }

    @Test
    public void funcPermitValidate_Test() {
        Map<String, String> map = new HashMap<>();
        map.put("apiUrl", "/urc/motan/service/api/IUrcService/logout");
        map.put(StringConstant.operator, "panyun");
        map.put(StringConstant.ticket, "b59ea6299ffec94100aa7d29b0d507d0");
        map.put(StringConstant.ip, "192.168.201.62");
//        map.put(StringConstant.funcVersion, "e76eab4b2d46b91dc1a009292106b1f4");
        // map.put(StringConstant.sysKey, "001");
        System.out.println("----------------------" + StringUtility.toJSONString_NoException(userService.funcPermitValidate(map)));
    }

    @Test
    public void login_Test() {
        UserVO authUser = new UserVO();
        authUser.userName = "panyun";
        authUser.pwd = "ASDFGhjkl;12345";
        authUser.ip = "pyIP";
        System.out.println("------LOGIN-----------------" + StringUtility.toJSONString_NoException(userService.login(authUser)));
    }

    @Test
    public void logout_Test() {
        Map<String, String> map = new HashMap<>();
        map.put("apiUrl", "/urc/motan/service/api/IUrcService/logout");
        map.put(StringConstant.operator, "panyun");
        map.put(StringConstant.ticket, "4146d43e2f587666f47b1bfdfbcd9c0a");
        map.put(StringConstant.ip, "192.168.201.62");
        map.put(StringConstant.funcVersion, "e76eab4b2d46b91dc1a009292106b1f4");
        String jsonStr = StringUtility.toJSONString_NoException(map);
        System.out.println(StringUtility.toJSONString_NoException(service.logout(jsonStr)));
    }

    @Autowired
    private DingApiProxy myDingApiProxy;

    public void syncDingOrgAndUser_Test() throws Exception {
        myDingApiProxy.getDingAccessToken();
    }

    public void getUserInfo_Test() throws Exception {
        // userBp.SynUserFromUserInfo("panyun");
        String httpOrgCreateTest = "https://userinfo.youkeshu.com/api/get_token";
        JSONObject object = new JSONObject();
        object.put("username", "panyun");
        object.put("password", "ASDFhjkl1234");
        String httpOrgCreateTestRtn = HttpUtility.sendPost(httpOrgCreateTest, object.toJSONString());
        System.out.println("result:" + httpOrgCreateTestRtn);
    }

    @Test
    public void assignAllPermit2Role_Test() {
        // 将所有系统的所有功能权限授予指定角色
        Long roleId = 1529746874242000015L;
        Map<String, Object> map = new HashMap<>();
        map.put(StringConstant.operator, "panyun");
        map.put("roleId", roleId);
        String jsonStr = StringUtility.toJSONString_NoException(map);
        MotanSession.initialSession(map);
        service.assignAllPermit2Role(jsonStr);
    }

    @Test
    public void updateUserPermitCache_Test() {
        List<String> lstUserName = new ArrayList<>();
        lstUserName.add("panyun");
        lstUserName.add("renmaohua");
        lstUserName.add("tangfeng");
        lstUserName.add("tangyong");
        lstUserName.add("weijie");
        lstUserName.add("yangbo");
        lstUserName.add("chenglifu");
        lstUserName.add("chensi");
        lstUserName.add("mengyuhua");
        lstUserName.add("chensi2");
        lstUserName.add("xieyi1");
        lstUserName.add("xieyi2");
        lstUserName.add("liujun");
        lstUserName.add("huangpeiqin");
        lstUserName.add("houyunfeng");
        lstUserName.add("panxi");

        lstUserName.clear();
        lstUserName.add("panyun");
        permitStatBp.updateUserPermitCache(lstUserName);
        // permitStatBp.updateUserPermitCache(lstUserName);
    }

    @Test
    public void importSysPermit_Test() throws IOException {
        String strJson1 = StringUtility.inputStream2String(ClassLoader.getSystemResourceAsStream("oms.json"));
        MotanSession.initialSession(strJson1);
        permissionService.importSysPermit(strJson1);
    }


    @Test
    public void testCache() {
        DistributedCacheBuilder b = DistributedCacheBuilder.newBuilder().config("/cache.properties");

        Cache cacheForever = b.cacheName("forever1").build();
//        Cache cacheTest = b.cacheName("cache").expire(50).build();
//        cacheTest.put("kobe", "bryant");

        Map<String, String> map = new HashMap<>();
        map.put("001", "URC");
        map.put("002", "OMS");
        String strKey = "sys_api_url_prefix";
        cacheForever.put("a", "b");
        cacheForever.put("a1", "b1");
        cacheForever.clear();
//        System.out.println("---->" + cacheForever.get(strKey));
    }

    private void put(Cache cacheForever, String strKey, Map<String, String> map) {
        if (map == null) return;
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            cacheForever.put(String.format("%s:%s", strKey, key), map.get(key));
        }
    }

    // @Test
    public void testSeq() {
        RoleDO roleDO = new RoleDO();
        roleDO.setRoleId(seqBp.getNextRoleId());
        roleDO.setRoleName("py");
        roleMapper.insert(roleDO);
        System.out.println(StringUtility.toJSONString_NoException(roleDO));
        // System.out.println(seqBp.getNextSeq("roleId"));
    }

    public void getAllFuc_Test() {
        Map<String, Object> map = new HashMap<>();
        map.put(StringConstant.operator, "linwanxian");
        String jsonStr = StringUtility.toJSONString_NoException(map);

        service.getAllFuncPermit(jsonStr);
    }

    public void createRole_Test() {
        Map<String, Object> map = new HashMap<>();
        map.put(StringConstant.operator, "panyun");
        RoleVO role = new RoleVO();
        role.roleName = "panyunTest2";
        map.put("role", role);
        String jsonStr = StringUtility.toJSONString_NoException(map);
        service.addOrUpdateRoleInfo(jsonStr);
    }

    @Test
    public void mybatis_log_Test() {
        Long roleId = 1529649147479000001L;
        RoleDO role = roleMapper.getRoleByRoleId(String.valueOf(roleId));
        System.out.println(StringUtility.toJSONString_NoException(role));
    }

}
