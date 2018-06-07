package com.yks.urc.entity;

import java.util.Date;

public class RoleDO {

    /**
     *
     */
    public String roleId;
    /**
     * 角色名
     */
    public String roleName;
    /**
     *  是否存活
     */
    public boolean isActive;
    /**
     *  是否通过验证
     */
    public boolean isAuthorizable;
    /**
     * 是否永久保存
     */
    public boolean isForever;
    /**
     *
     */
    public Date effectiveTime;
    /**
     *
     */
    public Date expireTime;
    /**
     *
     */
    public Date createdTime;
    /**
     *
     */
    public String createdBy;
    /**
     *
     */
    public Date updatedTime;
    /**
     *
     */
    public String updatedBy;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isAuthorizable() {
        return isAuthorizable;
    }

    public void setAuthorizable(boolean authorizable) {
        isAuthorizable = authorizable;
    }

    public boolean isForever() {
        return isForever;
    }

    public void setForever(boolean forever) {
        isForever = forever;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
