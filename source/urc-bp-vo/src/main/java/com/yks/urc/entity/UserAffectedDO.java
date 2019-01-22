package com.yks.urc.entity;

import java.util.Date;

/**
 * 版权：Copyright by www.youkeshu.com
 * 描述：代码注释以及格式化示例
 * 创建人：@author Songguanye
 * 创建时间：2019/1/17 14:59
 * 修改理由：
 * 修改内容：
 */
public class UserAffectedDO {
    private Long id;
    private String userName;
    private String createBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id=id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName=userName;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy=createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime=createTime;
    }

    private Date createTime;
}
