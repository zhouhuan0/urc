/**
 * 〈一句话功能简述〉<br>
 * 〈角色基础信息〉
 *
 * @author 31076
 * @create 2018/6/5
 * @since 1.0.0
 */
package com.yks.urc.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RoleVO implements Serializable {
    private static final long serialVersionUID = -706052625111152853L;
    
    public long roleId;
    
    /**
     *
     */
    public Integer id;
    /**
     * 角色名
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
    private String modifiedBy;

    /**
     * 备注
     */
    private String remark;

    /**
     * 功能权限json
     */
    public List<PermissionVO> selectedContext;
    /**
     * 拥有此角色的用户域账号
     */
    public List<String> lstUserName;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public List<PermissionVO> getSelectedContext() {
        return selectedContext;
    }

    public void setSelectedContext(List<PermissionVO> selectedContext) {
        this.selectedContext = selectedContext;
    }

    public List<String> getLstUserName() {
        return lstUserName;
    }

    public void setLstUserName(List<String> lstUserName) {
        this.lstUserName = lstUserName;
    }

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
