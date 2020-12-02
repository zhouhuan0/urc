package com.yks.urc.entity;

import java.util.Date;

public class PositionGroupVO {
    /**
     * 权限组名称
     */
    private String groupName;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 权限组Id
     */
    private String groupId;

    /**
     * 岗位名称
     */
    private String positionNames;

    /**
     * 用户名
     */
    private String userName;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPositionNames() {
        return positionNames;
    }

    public void setPositionNames(String positionNames) {
        this.positionNames = positionNames;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}