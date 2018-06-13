package com.yks.urc.vo.helper;

import java.util.List;

import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.urc.entity.Person;
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
		return getSuccessResult(null, CommonMessageCodeEnum.FAIL.getCode(), CommonMessageCodeEnum.FAIL.getDesc());
	}

	public static ResultVO getErrorResult(String state, String strMsg) {
		return getSuccessResult(null, state, strMsg);
	}

	public static <T> ResultVO getSuccessResult(T data ) {
		return getSuccessResult(data, CommonMessageCodeEnum.SUCCESS.getCode(), CommonMessageCodeEnum.SUCCESS.getDesc());
	}
}
