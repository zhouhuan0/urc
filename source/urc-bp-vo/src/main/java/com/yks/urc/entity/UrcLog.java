package com.yks.urc.entity;

import java.util.Date;

public class UrcLog {
    private Long id;

    private String userName;

    private Date operateTime;

    private String computerIp;

    private Integer moduleCode;

    private String operateAction;

    private String operateObject;

    private Date modifyTime;

    private String operateJson;
    
    

    
    /**
	 * 
	*/
		
	public UrcLog() {
		super();
	}

	/**
	 * @param id
	 * @param userName
	 * @param moduleName
	 * @param operateAction
	 * @param operateObject
	 * @param operateJson
	*/
		
	public UrcLog(String userName, Integer moduleCode, String operateAction, String operateObject,
			String operateJson) {
		super();
		this.userName = userName;
		this.moduleCode = moduleCode;
		this.operateAction = operateAction;
		this.operateObject = operateObject;
		this.operateJson = operateJson;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getComputerIp() {
        return computerIp;
    }

    public void setComputerIp(String computerIp) {
        this.computerIp = computerIp == null ? null : computerIp.trim();
    }


    public Integer getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(Integer moduleCode) {
		this.moduleCode = moduleCode;
	}

	public String getOperateAction() {
        return operateAction;
    }

    public void setOperateAction(String operateAction) {
        this.operateAction = operateAction == null ? null : operateAction.trim();
    }

    public String getOperateObject() {
        return operateObject;
    }

    public void setOperateObject(String operateObject) {
        this.operateObject = operateObject == null ? null : operateObject.trim();
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getOperateJson() {
        return operateJson;
    }

    public void setOperateJson(String operateJson) {
        this.operateJson = operateJson == null ? null : operateJson.trim();
    }

	@Override
	public String toString() {
		return "UrcLog [id=" + id + ", userName=" + userName + ", operateTime=" + operateTime + ", computerIp="
				+ computerIp + ", moduleCode=" + moduleCode + ", operateAction=" + operateAction + ", operateObject="
				+ operateObject + ", modifyTime=" + modifyTime + ", operateJson=" + operateJson + "]";
	}
    
}