/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/9
 * @since 1.0.0
 */
package com.yks.urc.service;

import com.yks.mq.client.MQConsumerClient;
import com.yks.mq.client.MQConsumerClient.MessageCallBack;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.entity.RoleDO;
import com.yks.urc.fw.EncryptHelper;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.mapper.IRoleMapper;
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
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//@Component
public class UrcServiceTest extends BaseServiceTest {
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

//	@Test
	public void mq_Test() {
		DataRuleVO dr = new DataRuleVO();
		dr.userName = "py";
		dr.lstDataRuleSys = new ArrayList<>();
		DataRuleSysVO e = new DataRuleSysVO();
		e.sysKey = "001";
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

	public void test_funcPermitValidate() {
		Map<String, String> map = new HashMap<>();
		map.put("apiUrl", "/api/grab/smt/batchMarking");
		map.put("moduleUrl", "/");
		map.put(StringConstant.operator, "dcadmin");
		map.put(StringConstant.ticket, "bfba159f79a0f4b77ee82fabd41507f2");
		map.put(StringConstant.ip, "pyIP");
		map.put(StringConstant.urcVersion, "eb1043692883ef9010cd6cdc8b624e90");
		map.put(StringConstant.sysKey, "001");
		System.out.println("----------------------" + userService.funcPermitValidate(map));
	}

	public void testLogin() {
		UserVO authUser = new UserVO();
		authUser.userName = "dcadmin";
		authUser.pwd = "Ldap_test";
		authUser.ip = "pyIP";
		System.out.println("------LOGIN-----------------" + StringUtility.toJSONString_NoException(userService.login(authUser)));
	}

	@Test
	public void testPermitCache() {
		List<String> lstUserName = new ArrayList<>();
		lstUserName.add("dcadmin");
		permitStatBp.updateUserPermitCache(lstUserName);
	}

	// @Test
	public void testCache() {
		List<String> lstFromDb = new ArrayList<>();
		lstFromDb.add("110");
		// cacheBp.insertUserSysKey("py", lstFromDb);
//		List<String> lst = cacheBp.getUserSysKey("py");
//		System.out.println("----------------SysKey:" + StringUtility.toJSONString_NoException(lst));
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

	public void test2() {

		String userName = "panyun";
		String sysKey = "001";
		List<String> lstRslt = userValidateBp.getFuncJsonLstByUserAndSysKey(userName, sysKey);
		System.out.print(userValidateBp.getFuncJsonByUserAndSysKey(userName, sysKey));
		// testSync();
	}
	@Test
	public void getUserInfo(){
		testSync();
	}
	@Transactional
	public void testSync() {
		String json="{\n" +
				"\t\"operator\":\"test3\"\n" +
				"}";ResultVO resultVO=service.syncUserInfo(json);
		System.out.println("=====================");
		System.out.println(resultVO.msg);
	}
	@Test
	public void getPlatformTest(){
		String json="{\n" +
				"\t\"operator\":\"test3\"\n" +
				"}";
		ResultVO resultVO=service.getPlatformList(json);
		System.out.println("=====================");
		System.out.println(resultVO.data + "," +resultVO.msg);
    }
	@Test
	public void searchUser(){
		String json ="{\n" +
				"\t\"operator\":\"test3\",\n" +
				"\t\"pageNumber\":0,\n" +
				"\t\"pageData\":10,\n" +
				"\t\"user\":{\n" +
				"\t\t\"userName\":\"linwanxian,panyun\"\n" +
				"\t}\n" +
				"}";
		ResultVO resultVO=service.getUsersByUserInfo(json);
		System.out.println("=================");
		System.out.println(resultVO.msg);
	}
	@Test
	public void getShopList(){
		String json ="{\n" +
				"\t\"operator\":\"test3\",\n" +
				"\t\"platform\":\"eBay\"\n" +
				"}";
		ResultVO resultVO =service.getShopList(json);
		System.out.println("==================");
		System.out.println(resultVO.data);
	}

	@Test
	public void getMyWay(){
        String json="{\n" +
                "\t\"operator\":\"panyun\"\n" +
                "}";
		ResultVO resultVO=service.getMyAuthWay(json);
        System.out.println("==================");
        System.out.println(resultVO.msg);
    }
}
