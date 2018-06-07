package com.yks.urc.entity;

import java.sql.Timestamp;
import java.util.Date;

public class RoleDO {
    /**
     * 角色名称
     */
    public String roleName;
    /**
     * 是否启用
     */
    public boolean isActive;
    /**
     * 是否管理员角色
     */
    public boolean isAuthorizable;
    /**
     * 是否永久有效
     */
    public boolean isForever;
    /**
     * 有效开始时间
     */
    public Date effectiveTime;
    /**
     * 有效结束时间
     */
    public Date expireTime;
    /**
     * 创建时间
     */
    public Date createTime;

    /**
     * 创建人
     */
    public String createBy;
    /**
     * 更新时间
     */
    public Date modifiedTime;
    /**
     * 更新人
     */
    public String modifiedBy;


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


    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
