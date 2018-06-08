package com.yks.urc.mapper;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yks.urc.ldap.bp.api.ILdapBp;

public class LdapTest extends BaseMapperTest {
	@Autowired
	ILdapBp IldapBp;

	@Test
	public void testInsert() {
		String userName = "panyun";
		String pwd = "ASDFhjkl1234";

		userName = "dcadmin";
		pwd = "Ldap_test";
		boolean blnRslt = IldapBp.validateUser(userName, pwd);
		System.out.println(blnRslt);
	}
}
