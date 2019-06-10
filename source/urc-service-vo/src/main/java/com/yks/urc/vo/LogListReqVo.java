package com.yks.urc.vo;

import java.util.Date;
import java.util.List;

public class LogListReqVo {

	private String moduleCode;
	private List<Long> operateTimeRange;
	private Integer pageData;
	private Integer pageNumber;
	private String userName;
	private Date operateTimeStart;
	private Date operateTimeEnd;
	private Integer moduleCode4Select;
	private Integer offset;
	
	
	
	
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getModuleCode4Select() {
		return moduleCode4Select;
	}
	public void setModuleCode4Select(Integer moduleCode4Select) {
		this.moduleCode4Select = moduleCode4Select;
	}
	public String getModuleCode() {
		return moduleCode;
	}
	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}
	public List<Long> getOperateTimeRange() {
		return operateTimeRange;
	}
	public void setOperateTimeRange(List<Long> operateTimeRange) {
		this.operateTimeRange = operateTimeRange;
	}
	public Integer getPageData() {
		return pageData;
	}
	public void setPageData(Integer pageData) {
		this.pageData = pageData;
	}
	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getOperateTimeStart() {
		return operateTimeStart;
	}
	public void setOperateTimeStart(Date operateTimeStart) {
		this.operateTimeStart = operateTimeStart;
	}
	public Date getOperateTimeEnd() {
		return operateTimeEnd;
	}
	public void setOperateTimeEnd(Date operateTimeEnd) {
		this.operateTimeEnd = operateTimeEnd;
	}
	@Override
	public String toString() {
		return "LogListReqVo [moduleCode=" + moduleCode + ", operateTimeRange=" + operateTimeRange + ", pageData="
				+ pageData + ", pageNumber=" + pageNumber + ", userName=" + userName + ", operateTimeStart="
				+ operateTimeStart + ", operateTimeEnd=" + operateTimeEnd + "]";
	}
	
}
