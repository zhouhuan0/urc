/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/9
 * @since 1.0.0
 */
package com.yks.urc.service;

import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.entity.RoleDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.motan.service.impl.UrcServiceImpl;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.seq.bp.api.ISeqBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.UserVO;

import java.util.ArrayList;
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
	@Autowired
	private ISeqBp seqBp;

	@Autowired
	IRoleMapper roleMapper;

	@Autowired
	private ICacheBp cacheBp;

	@Autowired
	private IPermitStatBp permitStatBp;

	@Test
	public void testPermitCache() {
		List<String> lstUserName = new ArrayList<>();
		lstUserName.add("panyun");
		permitStatBp.updateUserPermitCache(lstUserName);
	}

	// @Test
	public void testCache() {
		List<String> lstFromDb = new ArrayList<>();
		lstFromDb.add("110");
		// cacheBp.insertUserSysKey("py", lstFromDb);
		List<String> lst = cacheBp.getUserSysKey("py");
		System.out.println("----------------SysKey:" + StringUtility.toJSONString_NoException(lst));
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

	@Transactional
	public void testSync() {
		UserVO userVO = new UserVO();
		userVO.userName = "lwx";
		service.syncUserInfo(userVO);
		System.out.println(111);
	}

}
