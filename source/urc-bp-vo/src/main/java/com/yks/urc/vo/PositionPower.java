package com.yks.urc.vo;

import java.util.Date;

/**
 * 岗位权限导出vo
 */
public class PositionPower {
    /**
     * 岗位
     */
    private String positionName;
    /**
     * 平台类型
     */
    private String platformType;

    /**
     * 系统名称
     */
    private String systemName;

    /**
     * 权限名称
     */
    private String permitName;

    /**
     * 分配人
     */
    private String distributionMan;

    /**
     * 最新分配时间
     */
    private Date lastDate;

    private String sysContext;

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getPermitName() {
        return permitName;
    }

    public void setPermitName(String permitName) {
        this.permitName = permitName;
    }

    public String getDistributionMan() {
        return distributionMan;
    }

    public void setDistributionMan(String distributionMan) {
        this.distributionMan = distributionMan;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    public String getSysContext() {
        return sysContext;
    }

    public void setSysContext(String sysContext) {
        this.sysContext = sysContext;
    }
}
