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
    private Integer sysType;

    private List<String> sysKeys;

	public List<String> getSysKeys() {
		return sysKeys;
	}

	public void setSysKeys(List<String> sysKeys) {
		this.sysKeys = sysKeys;
	}

	public Integer getSysType() {
		return sysType;
	}

	public void setSysType(Integer sysType) {
		this.sysType = sysType;
	}

	@Override
	public String toString() {
		return "SysKeysVO [sysKeys=" + sysKeys + "]";
	}

}
