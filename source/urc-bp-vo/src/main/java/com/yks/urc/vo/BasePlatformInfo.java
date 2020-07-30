package com.yks.urc.vo;

import java.util.Date;

/**
 * 平台信息表
 */
public class BasePlatformInfo {
    private Integer id;
    /**
     * 平台编码
     */
    private String platformCode;
    /**
     *
     * 中文名
     */
    private String platformNameCn;
    /**
     * 英文名
     */
    private String platformNameEn;

    /**
     * 平台编码（旧）
     */
    private String platformCodeOld;
    /**
     * 是否允许单角色多用户（0 否 1 是 默认 0）
     */
    private Boolean ifSingleRolesMultipleUsers;

    private String creater;

    private Date createDate;

    private String modifier;

    private Date modifyDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode == null ? null : platformCode.trim();
    }

    public String getPlatformCodeOld() {
        return platformCodeOld;
    }

    public void setPlatformCodeOld(String platformCodeOld) {
        this.platformCodeOld = platformCodeOld;
    }

    public String getPlatformNameCn() {
        return platformNameCn;
    }

    public void setPlatformNameCn(String platformNameCn) {
        this.platformNameCn = platformNameCn == null ? null : platformNameCn.trim();
    }

    public String getPlatformNameEn() {
        return platformNameEn;
    }

    public void setPlatformNameEn(String platformNameEn) {
        this.platformNameEn = platformNameEn == null ? null : platformNameEn.trim();
    }

    public Boolean getIfSingleRolesMultipleUsers() {
        return ifSingleRolesMultipleUsers;
    }

    public void setIfSingleRolesMultipleUsers(Boolean ifSingleRolesMultipleUsers) {
        this.ifSingleRolesMultipleUsers = ifSingleRolesMultipleUsers;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater == null ? null : creater.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier == null ? null : modifier.trim();
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
}