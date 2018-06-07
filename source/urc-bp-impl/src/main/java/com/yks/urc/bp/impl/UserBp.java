package com.yks.urc.bp.impl;

import org.springframework.stereotype.Component;

@Component
public class UserBp {
	/**
	 * token 请求地址
	 */
	private static final  String GET_TOKEN ="https://userinfo.youkeshu.com/api/get_token";
	/**
	 * 获取钉钉信息地址
	 */
	private  static  final  String DING_DING ="http://cf.youkeshu.com/pages/viewpage.action?pageId=2556124";
	public void SynUserFromUserInfo() {
		//1.只调用UserInfo接口，同步UserInfo数据
	}
}
