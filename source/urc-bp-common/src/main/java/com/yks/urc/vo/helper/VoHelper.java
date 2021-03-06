package com.yks.urc.vo.helper;

import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.vo.ResultVO;

/**
 * 实体类创建
 * 
 * @author panyun@youkeshu.com
 * @date 2018年6月6日 上午11:06:51
 * 
 */
public class VoHelper {

	public static boolean ifSuccess(ResultVO rslt) {
		return rslt != null && rslt.state.equalsIgnoreCase(CommonMessageCodeEnum.SUCCESS.getCode());
	}

	public static ResultVO getSuccessResult() {
		return getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), CommonMessageCodeEnum.SUCCESS.getDesc(), null);
	}

	public static <T> ResultVO getSuccessResult(String msg) {
		return getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), msg, null);
	}

	public static ResultVO getResultVO(String state, String strMsg) {
		return getResultVO(state, strMsg, null);
	}

	public static ResultVO getFail(String msg) {
		return getResultVO(CommonMessageCodeEnum.FAIL.getCode(), msg, null);
	}

	public static <T> ResultVO getResultVO(String state, String strMsg, T data) {
		ResultVO<T> rslt = new ResultVO<>();
		rslt.data = data;
		rslt.state = state;
		rslt.msg = strMsg;
		return rslt;
	}

	public static ResultVO getErrorResult() {
		return getResultVO(CommonMessageCodeEnum.FAIL.getCode(), CommonMessageCodeEnum.FAIL.getDesc(), null);
	}

	public static ResultVO getErrorResult(String state, String strMsg) {
		return getResultVO(state, strMsg, null);
	}

	public static <T> ResultVO getSuccessResult(T data) {
		return getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), CommonMessageCodeEnum.SUCCESS.getDesc(), data);
	}

	public static ResultVO getResultVO(ErrorCode err, String strMsg) {
		return getResultVO(err.getState(), strMsg);
	}

	public static <T> ResultVO getResultVO(ErrorCode err, String strMsg, T data) {
		return getResultVO(err.getState(), strMsg, data);
	}
}
