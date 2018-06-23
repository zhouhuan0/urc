package com.yks.urc.vo.helper;

import java.util.LinkedHashMap;
import java.util.Map;

import com.yks.urc.fw.StringUtility;

/**
 * 查询参数
 */
public class Query extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	// 
	private int offset;
	// 每页条数
	private int limit;

	public Query(Object obj,int pageNumber, int pageData) {
		if(obj!=null){
			Map<String, Object> params= StringUtility.ConvertObjToMap(obj);
			this.putAll(params);
		}
		// 分页参数
		this.limit = (pageData==0 ? 20 : pageData);
		this.offset = (pageNumber==0 ? 0 :  ((pageNumber - 1)*this.limit));
		this.put("offset", offset);
		this.put("page", offset / limit + 1);
		this.put("limit", limit);
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.put("offset", offset);
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}
