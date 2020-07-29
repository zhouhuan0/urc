package com.yks.urc.vo;

import java.util.List;

/**
 * @ProjectName: actmgr
 * @Package: com.yks.actmgr.third
 * @ClassName: AccountInfo4Third
 * @Author: zengzheng
 * @Description: 返回第三方账号信息体
 * @Date: 2020/7/22 17:02
 * @Version: 1.0
 */
public class AccountInfo4Third {
    /**
     * 子账号ID 唯一值
     */
    private String accountId;

    /**
     * 子账号名称
     */
    private String accountName;

    /**
     * 平台编码
     */
    private String platformCode;

    /**
     * 站点
     */
    private String site;

    /**
     * 账号状态
     */
    private Integer accountStatus;

    /**
     * 一级部门
     */
    private String firstDepartmentName;


    /**
     * 账号角色用户信息
     */
    private List<RoleUserInfo> roleUserInfoList;

    private String modifyDate;



    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Integer getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(Integer accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getFirstDepartmentName() {
        return firstDepartmentName;
    }

    public void setFirstDepartmentName(String firstDepartmentName) {
        this.firstDepartmentName = firstDepartmentName;
    }

    public List<RoleUserInfo> getRoleUserInfoList() {
        return roleUserInfoList;
    }

    public void setRoleUserInfoList(List<RoleUserInfo> roleUserInfoList) {
        this.roleUserInfoList = roleUserInfoList;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }
}
