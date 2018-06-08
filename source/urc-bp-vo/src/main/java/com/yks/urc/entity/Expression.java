package com.yks.urc.entity;

import java.util.Date;

public class Expression {
    private Long id;

    private Long sqlId;

    private String fieldCode;

    private String entityCode;

    private String oper;

    private Long parentExpressionId;

    private Byte isAnd;

    private Date createTime;

    private String createBy;

    private Date modifiedTime;

    private String modifiedBy;

    private String operValues;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSqlId() {
        return sqlId;
    }

    public void setSqlId(Long sqlId) {
        this.sqlId = sqlId;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode == null ? null : fieldCode.trim();
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode == null ? null : entityCode.trim();
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper == null ? null : oper.trim();
    }

    public Long getParentExpressionId() {
        return parentExpressionId;
    }

    public void setParentExpressionId(Long parentExpressionId) {
        this.parentExpressionId = parentExpressionId;
    }

    public Byte getIsAnd() {
        return isAnd;
    }

    public void setIsAnd(Byte isAnd) {
        this.isAnd = isAnd;
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

    public String getOperValues() {
        return operValues;
    }

    public void setOperValues(String operValues) {
        this.operValues = operValues == null ? null : operValues.trim();
    }
}