package com.yks.urc.ldap.bp.impl;

import org.springframework.stereotype.Component;

@Component
public class LdapBpImpl {
	public boolean validateUser(String userName, String pwd) {
		return LDAPUtil.connect(false, userName, pwd, null);
	}
}
