/**
 * 〈一句话功能简述〉<br>
 * 〈系统数据权限〉
 *
 * @author 31076
 * @create 2018/6/5
 * @since 1.0.0
 */
package com.yks.urc.vo;

import java.io.Serializable;
import java.util.List;

public class DataRuleSysVO  implements Serializable{
    private static final long serialVersionUID = 6871231447375008701L;
    /**
     * 系统key
     */
    public String sysKey;
    /**
     *
     */
    public List<DataRuleSqlVO> urcSqlDOList;

    public String getSysKey() {
        return sysKey;
    }

    public void setSysKey(String sysKey) {
        this.sysKey = sysKey;
    }

    public List<DataRuleSqlVO> getUrcSqlDOList() {
        return urcSqlDOList;
    }

    public void setUrcSqlDOList(List<DataRuleSqlVO> urcSqlDOList) {
        this.urcSqlDOList = urcSqlDOList;
    }
}
