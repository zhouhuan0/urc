package com.yks.urc.motan.service.api;

import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;

public interface IUrcService {
	/**
	 * 同步数据
	 * @param curUser
	 * @return
	 */
	ResultVO syncUserInfo(UserVO curUser);

	/**
	 * 登陆校验：用户名密码检验
	 * @param curUser
	 * @param authUser
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月6日 下午12:22:35
	 */
	ResultVO login(UserVO curUser, UserVO authUser);
}
