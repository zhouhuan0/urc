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

public class SearchSellerIdRespVO{
    
    private List<String> list;

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "SearchSellerIdRespVO [list=" + list + "]";
	}

}
