package com.yks.urc.vo.helper;

import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.urc.vo.ResultVO;

/**
 * 实体类创建
 * 
 * @author panyun@youkeshu.com
 * @date 2018年6月6日 上午11:06:51
 * 
 */
public class VoHelper {
	public static ResultVO getSuccessResult() {
		return getSuccessResult(null, CommonMessageCodeEnum.SUCCESS.getCode(), CommonMessageCodeEnum.SUCCESS.getDesc());
	}

	public static <T> ResultVO getSuccessResult(T data, String state, String strMsg) {
		ResultVO<T> rslt = new ResultVO<>();
		rslt.data = data;
		rslt.state = state;
		rslt.msg = strMsg;
		return rslt;
	}
	public static ResultVO getErrorResult() {
		return getSuccessResult(null, UrCenterStatusEnum.FAIL.getCode(), UrCenterStatusEnum.FAIL.getDesc());
	}
}
