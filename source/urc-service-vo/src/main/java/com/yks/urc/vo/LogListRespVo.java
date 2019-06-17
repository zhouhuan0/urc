package com.yks.urc.vo;

import java.util.List;

public class LogListRespVo {

	private List<UrcLogVO> list;
	private Integer pageSize;
	private Integer total;
	private Integer totalPage;
	public List<UrcLogVO> getList() {
		return list;
	}
	public void setList(List<UrcLogVO> list) {
		this.list = list;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}
	@Override
	public String toString() {
		return "LogListRespVo [list=" + list + ", pageSize=" + pageSize + ", total=" + total + ", totalPage="
				+ totalPage + "]";
	}
	
}
