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

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoClientApplication.class)
public class MotanUserServiceTest {

	@MotanReferer
	private IUrcService urcService;

	@Test
	public void testSayHello() {
		try {
			UserVO curUser = new UserVO();
			curUser.ip = "127.0.0.1";

			UserVO authUser = new UserVO();
			authUser.userName = "panyun";
			authUser.pwd = "ASDFhjkl1234";

			// com.yks.urc.motan.service.api.IUrcService
			ResultVO rslt = urcService.login(curUser, authUser);
			System.out.println(StringUtility.toJSONString_NoException(rslt));

//			for (int i = 0; i < 10; i++) {
//				authUser.userName = "panyun" + i;
//				urcService.login(curUser, authUser);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
