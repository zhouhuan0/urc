package com.yks.urc.vo;

/**
 * 获得用户和岗位
 */
public class UserByPosition {
    /**
     * 岗位id
     */
    private String positionId;
    /**
     * 岗位名称
     */
    private String positionName;
    /**
     * 账号
     */
    private String userName;

    /**
     * 是否启用:0-禁用,1-启用
     */
    private Integer isActive;

    /**
     * 权限名称
     */
    private String permitName;

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getPermitName() {
        return permitName;
    }

    public void setPermitName(String permitName) {
        this.permitName = permitName;
    }
}
