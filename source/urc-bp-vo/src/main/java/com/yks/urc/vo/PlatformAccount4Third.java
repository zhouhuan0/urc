package com.yks.urc.vo;

import java.util.List;

/**
 * @ProjectName: actmgr
 * @Package: com.yks.actmgr.third
 * @ClassName: PlatformAccount4Third
 * @Author: zengzheng
 * @Description:
 * @Date: 2020/7/28 19:24
 * @Version: 1.0
 */
public class PlatformAccount4Third {
    /**
     * 平台
     */
    private String platformCode;

    /**
     * 是否全选 0 否 1 是
     */
    private int ifAll = 0;

    /**
     * 账号列表
     */
    private List<AccountBeanInfoVO> accountList;


    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public int getIfAll() {
        return ifAll;
    }

    public void setIfAll(int ifAll) {
        this.ifAll = ifAll;
    }

    public List<AccountBeanInfoVO> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<AccountBeanInfoVO> accountList) {
        this.accountList = accountList;
    }
}
