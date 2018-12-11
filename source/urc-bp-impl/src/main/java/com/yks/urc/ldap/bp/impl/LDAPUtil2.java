package com.yks.urc.ldap.bp.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

@Component
public class LDAPUtil2 {
    @Value("${ldap.uri}")
	private String ldapURL;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public boolean connect(String username, String password) {

		DirContext ctx = null;
		try {
			Hashtable<String, Object> env = new Hashtable<String, Object>();
			env.put(Context.SECURITY_AUTHENTICATION, "simple");//一种模式，不用管，就这么写就可以了
			env.put(Context.SECURITY_PRINCIPAL, username);
			env.put(Context.SECURITY_CREDENTIALS, password);
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, ldapURL);
			ctx = new InitialDirContext(env);
			return true;
		} catch (Exception e) {
			logger.error(String.format("connect:%s %s", username, password), e);
		}
		finally {
			try {
				if(ctx!=null) {
					ctx.close();
				}
			} catch (Exception e) {
				logger.error("LdapContext close ERROR",e);
			}
		}
		return false;
	}
}