package com.yks.urc.ldap.bp.impl;

import java.util.Arrays;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.springframework.util.StringUtils;

public class LDAPUtil {

	private static final String ADMIN_USER = "dcadmin";
	private static final String ADMIN_PWD = "Ldap_test";
	private static final Control[] connCtls = null;
	// private static final String ldapURL =
	// "LDAP://192.168.5.112:";//格式：LDAP://IP:port 192.168.5.112:389;
	private static final String ldapURL = "LDAP://youkeshu.com:";// 格式：LDAP://IP:port LDAP://youkeshu.com:389;
	private static final String ldapPort = "389";
	private static final String ldapSSLPort = "636";
	private static final String ou = "youkeshu";
	// private static final String root = "OU=youkeshu,DC=photo138,DC=com";
	private static final String root = "OU=youkeshu,DC=youkeshu,DC=com";
	/** connect to ldap */
	private static DirContext dc = null;

	public static Hashtable getEnv(boolean isAdmin, String userName, String password, String protocol) {
		Hashtable<String, String> HashEnv = new Hashtable<String, String>();
		String user = null;
		String pwd = null;
		String port = ldapPort;
		if (isAdmin) {
			user = ADMIN_USER;
			pwd = ADMIN_PWD;
		} else {
			user = userName;
			pwd = password;
		}

		HashEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		HashEnv.put(Context.SECURITY_AUTHENTICATION, "simple");// "none","simple","strong"
		HashEnv.put(Context.SECURITY_PRINCIPAL, user);
		HashEnv.put(Context.SECURITY_CREDENTIALS, pwd);

		if (!StringUtils.isEmpty(protocol) && "ssl".equals(protocol)) {

			String keystore = System.getProperty("java.home") + "\\lib\\security\\cacerts";

			System.setProperty("javax.net.ssl.trustStore", keystore);
			System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
			HashEnv.put(Context.SECURITY_PROTOCOL, protocol);
			port = ldapSSLPort;
		}
		HashEnv.put(Context.PROVIDER_URL, ldapURL + port);
		return HashEnv;
	}

	/** connect to ldap */
	public static boolean connect(boolean isAdmin, String username, String password, String protocol) {

		LdapContext ctx = null;
		String user = null;
		try {
			ctx = new InitialLdapContext(getEnv(isAdmin, username, password, protocol), connCtls);

			if (isAdmin) {
				user = ADMIN_USER;
			} else {
				user = username;
			}
			System.out.println(user + " <<<:[connect success]:>>>" + protocol);
			return true;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			System.out.println(user + " <<<:[connect fail]:>>>" + protocol);
		}

		return false;
	}

}