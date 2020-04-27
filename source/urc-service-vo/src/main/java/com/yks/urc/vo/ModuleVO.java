package com.yks.urc.vo;

import java.util.List;

public class ModuleVO {
	public String name;
	public String key;
	public String url;
	public Integer show;
	public Integer sortIdx;
	public List<ModuleVO> module;
	public List<FunctionVO> function;

	public String sysKey;
	public List<String> lstChildFunc;
	public StringBuilder pageFullPathName = new StringBuilder();

}
