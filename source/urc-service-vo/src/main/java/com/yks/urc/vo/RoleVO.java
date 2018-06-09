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
    /**
     * 功能权限json
     */
    public String selectedContext;
    /**
     * 拥有些角色的用户域账号
     */
    public List<String> lstUserName;






}
