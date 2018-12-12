package com.yks.urc.ldap.bp.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.ldap.bp.api.ILdapBp;

@Component
public class LdapBpImpl implements ILdapBp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	LDAPUtil2 ldapUtil;

	public boolean validateUser(String userName, String pwd) {
		if (StringUtility.isNullOrEmpty(userName) || StringUtility.isNullOrEmpty(pwd))
			return false;
		int i=0;
		while (i < 3) {
			if (ldapUtil.connect(userName, pwd)) {
				return true;
			}
			i++;
		}
		logger.info(String.format("loginFailure:%s|%s", userName, pwd));
		return false;
	}
}