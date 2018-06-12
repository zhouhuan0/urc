/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/9
 * @since 1.0.0
 */
package com.yks.urc.service;

import com.yks.urc.motan.service.impl.UrcServiceImpl;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.UserVO;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UrcServiceTest extends BaseServiceTest {
	@Autowired
	private UrcServiceImpl service;
	@Autowired
	private IUserValidateBp userValidateBp;

	@Test
	public void test2() {
	/*	List<String> lstRslt = userValidateBp.getFuncJsonByUserAndSysKey("panyun", "001");
		System.out.print(lstRslt.get(0));*/
		// testSync();
	}

	@Transactional
	public void testSync() {
		UserVO userVO = new UserVO();
		userVO.userName = "lwx";
		service.syncUserInfo(userVO);
		System.out.println(111);
	}
}
