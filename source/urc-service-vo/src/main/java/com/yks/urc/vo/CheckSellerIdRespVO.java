/**
 * 〈一句话功能简述〉<br>
 * 〈平台账号站点数据〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/7/7
 * @since 1.0.0
 */
package com.yks.urc.vo;

import java.util.List;

public class CheckSellerIdRespVO{
    
    private List<String> notOkSellerId;
    
    private List<String> okSellerId;

	public List<String> getNotOkSellerId() {
		return notOkSellerId;
	}

	public void setNotOkSellerId(List<String> notOkSellerId) {
		this.notOkSellerId = notOkSellerId;
	}

	public List<String> getOkSellerId() {
		return okSellerId;
	}

	public void setOkSellerId(List<String> okSellerId) {
		this.okSellerId = okSellerId;
	}

	@Override
	public String toString() {
		return "CheckSellerIdRespVO [notOkSellerId=" + notOkSellerId + ", okSellerId=" + okSellerId + "]";
	}
    
}
