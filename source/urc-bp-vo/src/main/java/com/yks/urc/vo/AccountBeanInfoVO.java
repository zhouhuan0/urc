package com.yks.urc.vo;

/**
 * @ProjectName: actmgr
 * @Package: com.yks.actmgr.vo.base
 * @ClassName: AccountBeanInfoVO
 * @Author: zengzheng
 * @Description: 查询账号实体VO
 * @Date: 2020/7/23 16:07
 * @Version: 1.0
 */
public class AccountBeanInfoVO {
    private String accountId;
    private String accountName;

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
}
