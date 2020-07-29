package com.yks.urc.vo;

import java.util.List;

/**
 * @ProjectName: actmgr
 * @Package: com.yks.actmgr.third
 * @ClassName: Request4GetAccountInfo
 * @Author: zengzheng
 * @Description: getAccountInfo请求体
 * @Date: 2020/7/13 12:26
 * @Version: 1.0
 */
public class Request4GetAccountInfo {
    /**
     *账号Id集合
     */
    private List<String> accountIds;

    /**
     * 子账号名
     */
    private List<String> accountNames;

    /**
     * 平台编码
     */
    private String platformCode;

    /**
     * 站点编码
     */
    private String site;

    /**
     * 更新时间（开始）
     */
    private String modifyDateStart;

    /**
     * 更新时间（结束）
     */
    private String modifyDateEnd;

    /**
     * 当前页码
     */
    private int pageNo;

    /**
     * 每页条数
     */
    private int pageSize;

    public List<String> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(List<String> accountIds) {
        this.accountIds = accountIds;
    }

    public List<String> getAccountNames() {
        return accountNames;
    }

    public void setAccountNames(List<String> accountNames) {
        this.accountNames = accountNames;
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

    public String getModifyDateStart() {
        return modifyDateStart;
    }

    public void setModifyDateStart(String modifyDateStart) {
        this.modifyDateStart = modifyDateStart;
    }

    public String getModifyDateEnd() {
        return modifyDateEnd;
    }

    public void setModifyDateEnd(String modifyDateEnd) {
        this.modifyDateEnd = modifyDateEnd;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
