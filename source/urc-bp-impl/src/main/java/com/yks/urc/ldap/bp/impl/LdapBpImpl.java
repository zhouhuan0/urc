package com.yks.urc.ldap.bp.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.ldap.bp.api.ILdapBp;

@Component
public class LdapBpImpl implements ILdapBp {
	@Autowired
	LDAPUtil ldapUtil;

	public boolean validateUser(String userName, String pwd) {
		if (StringUtility.isNullOrEmpty(userName) || StringUtility.isNullOrEmpty(pwd))
			return false;
		return ldapUtil.connect(false, userName, pwd, null);
	}
}
