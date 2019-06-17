package com.yks.urc.vo;

public class LogModuleVo {

	private String key;
	private String label;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	@Override
	public String toString() {
		return "LogModuleVo [key=" + key + ", label=" + label + "]";
	}
	
}
