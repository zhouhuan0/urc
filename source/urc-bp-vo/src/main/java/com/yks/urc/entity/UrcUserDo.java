/**
 * 〈一句话功能简述〉<br>
 * 〈用户域账号表〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/8
 * @since 1.0.0
 */
package com.yks.urc.entity;

import java.util.Date;

public class UrcUserDo {
    public int id;
    /**
     * 上网账号
     */
    public String username;
    /**
     * 表示是否启用， 66050表示禁用，其他的表示启用。
     */
    public int isActive;
    /**
     * 启用时间
     */
    public Date activeTime;
    /**
     * 钉钉用户id
     */
    public String dingUserId;
    /**
     * 创建人
     */
    public String createBy;

    public String createTime;

    /**
     * 修改人
     */
    public String modifiedBy;
    public String modifiedTime;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public Date getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(Date activeTime) {
        this.activeTime = activeTime;
    }

    public String getDingUserId() {
        return dingUserId;
    }

    public void setDingUserId(String dingUserId) {
        this.dingUserId = dingUserId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

}
