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
}
