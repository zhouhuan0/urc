package com.yks.urc.vo;

import java.util.Date;

public class UrcLogVO {
    private Long id;

    private String userName;

    private String operateTime;

    private String computerIp;

    private Integer moduleCode;

    private String operateAction;

    private String operateObject;

    
    private String moduleName;

    

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


    public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
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

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	@Override
	public String toString() {
		return "UrcLogVO [id=" + id + ", userName=" + userName + ", operateTime=" + operateTime + ", computerIp="
				+ computerIp + ", moduleCode=" + moduleCode + ", operateAction=" + operateAction + ", operateObject="
				+ operateObject + ", moduleName=" + moduleName + "]";
	}

}