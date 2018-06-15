package com.yks.demo.motan.service;

import com.alibaba.fastjson.JSONObject;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;
import com.yks.demo.DemoClientApplication;
import com.yks.demo.bean.UserInfo;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.vo.LoginRespVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserSysVO;
import com.yks.urc.vo.UserVO;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoClientApplication.class)
public class MotanUserServiceTest {

	@MotanReferer
	private IUrcService urcService;

	// @Test
	public void testSayHello() {
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("userName", "dcadmin");
			map.put("pwd", "Ldap_test");
			map.put("ip", "127.0.0.1");
			String rslt = urcService.login(map);
			System.out.println(rslt);

			// for (int i = 0; i < 10; i++) {
			// authUser.userName = "panyun" + i;
			// urcService.login(curUser, authUser);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void testGetAllFuncPermit() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("operator", "dcadmin");
		String jsonStr = StringUtility.toJSONString_NoException(map);
		System.out.println((urcService.getAllFuncPermit(jsonStr)));
	}

	public void test_funcPermitValidate() {
		Map<String, String> map = new HashMap<>();
		map.put("apiUrl", "/api/grab/smt/batchMarking");
		map.put("moduleUrl", "/");
		map.put(StringConstant.operator, "dcadmin");
		map.put(StringConstant.ticket, "3e0bff735cc2349452f0137317c200c1");
		map.put(StringConstant.ip, "pyIP");
		map.put(StringConstant.urcVersion, "eb1043692883ef9010cd6cdc8b624e90");
		map.put(StringConstant.sysKey, "001");
		System.out.println("----------------------" + urcService.funcPermitValidate(map));
	}

	@Test
	public void testLogin() {
		// 登陆+获取功能权限版本号+鉴权
		String ip = "192.168.201.62";
		Map<String, String> map = new HashMap<>();
		UserVO authUser = new UserVO();
		map.put("userName", "dcadmin");
		map.put("pwd", "Ldap_test");
		map.put("ip", ip);
		String strResp = urcService.login(map);

		System.out.println("------LOGIN-----------------" + strResp);
		// ResultVO<LoginRespVO> loginResp = new ResultVO<LoginRespVO>();
		JSONObject loginResp = StringUtility.parseString(strResp);
		map.put("operator", "dcadmin");
		String jsonStr = StringUtility.toJSONString_NoException(map);
		String strRespAllFunc = urcService.getAllFuncPermit(jsonStr);
		System.out.println("------getAllFuncPermit-----------------" + strRespAllFunc);

		JSONObject allFuncResp = StringUtility.parseString(strRespAllFunc);
		UserSysVO[] arrUserSysVO = StringUtility.parseObject(allFuncResp.getJSONArray("data").toJSONString(), new UserSysVO[0].getClass());
		String strSysKey = "004";
		UserSysVO uSys = null;
		for (UserSysVO u : arrUserSysVO) {
			if (StringUtility.stringEqualsIgnoreCase(strSysKey, u.sysKey)) {
				uSys = u;
				break;
			}
		}
		map.put("apiUrl", "/api/grab/smt/batchMarking");
		map.put("moduleUrl", "/");
		map.put(StringConstant.operator, "dcadmin");
		map.put(StringConstant.ticket, loginResp.getJSONObject("data").getString("ticket"));// "3e0bff735cc2349452f0137317c200c1");
		map.put(StringConstant.ip, ip);
		map.put(StringConstant.urcVersion, uSys.funcVersion);// "eb1043692883ef9010cd6cdc8b624e90");
		map.put(StringConstant.sysKey, strSysKey);
		System.out.println("------funcPermitValidate----------------" + urcService.funcPermitValidate(map));
	}
}
