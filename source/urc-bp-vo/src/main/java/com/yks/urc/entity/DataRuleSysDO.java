package com.yks.urc.entity;

import java.util.Date;

public class DataRuleSysDO {
    private Long id;

    private Long dataRuleSysId;

    private Long dataRuleId;

    private String sysKey;

    private Date createTime;

    private String createBy;

    private Date modifiedTime;

    private String modifiedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDataRuleSysId() {
        return dataRuleSysId;
    }

    public void setDataRuleSysId(Long dataRuleSysId) {
        this.dataRuleSysId = dataRuleSysId;
    }

    public Long getDataRuleId() {
        return dataRuleId;
    }

    public void setDataRuleId(Long dataRuleId) {
        this.dataRuleId = dataRuleId;
    }

    public String getSysKey() {
        return sysKey;
    }

    public void setSysKey(String sysKey) {
        this.sysKey = sysKey == null ? null : sysKey.trim();
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
        this.createBy = createBy == null ? null : createBy.trim();
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
        this.modifiedBy = modifiedBy == null ? null : modifiedBy.trim();
    }
}