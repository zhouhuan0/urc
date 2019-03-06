package com.yks.urc.vo;

import java.io.Serializable;

/**
 * 版权：Copyright by www.youkeshu.com
 * 描述：代码注释以及格式化示例
 * 创建人：@author Songguanye
 * 创建时间：2019/1/26 9:38
 * 修改理由：
 * 修改内容：
 */
public class SystemKeyContext{
    private String selectedContext;
    private String sysKey;

    public String getSelectedContext() {
        return selectedContext;
    }

    public void setSelectedContext(String selectedContext) {
        this.selectedContext=selectedContext;
    }

    public String getSysKey() {
        return sysKey;
    }

    public void setSysKey(String sysKey) {
        this.sysKey=sysKey;
    }
}
