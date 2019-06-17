/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author lwx
 * @create 2018/11/2
 * @since 1.0.0
 */
package com.yks.urc.vo;

import java.util.List;

public class SysKeysVO {


    private List<String> sysKeys;

	public List<String> getSysKeys() {
		return sysKeys;
	}

	public void setSysKeys(List<String> sysKeys) {
		this.sysKeys = sysKeys;
	}

	@Override
	public String toString() {
		return "SysKeysVO [sysKeys=" + sysKeys + "]";
	}

}
