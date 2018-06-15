/**
 * 〈一句话功能简述〉<br>
 * 〈数据权限模板〉
 *
 * @author 31076
 * @create 2018/6/5
 * @since 1.0.0
 */
package com.yks.urc.vo;

import java.io.Serializable;
import java.util.List;

public class DataRuleTemplVO implements Serializable {
    private static final long serialVersionUID = 824547161363763919L;
    /**
     * 模板ID
     */
    private String templId;
    /**
     *  创建人
     */
    public String userName;
    /**
     * 模板名称
     */
    public String templName;
    /**
     * 备注
     */
    public String remark;
    /**
     * 一个系统一个VO
     */
    public List<DataRuleSysVO> lstDataRuleSys;

    public String getTemplId() {
        return templId;
    }

    public void setTemplId(String templId) {
        this.templId = templId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTemplName() {
        return templName;
    }

    public void setTemplName(String templName) {
        this.templName = templName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<DataRuleSysVO> getLstDataRuleSys() {
        return lstDataRuleSys;
    }

    public void setLstDataRuleSys(List<DataRuleSysVO> lstDataRuleSys) {
        this.lstDataRuleSys = lstDataRuleSys;
    }
}
