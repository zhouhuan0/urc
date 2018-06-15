package com.yks.demo.motan.service;

import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;
import com.yks.demo.DemoClientApplication;
import com.yks.demo.bean.UserInfo;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.vo.ResultVO;
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

	@Test
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
}
