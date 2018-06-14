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
    public String sysName;
    /**
     * 行权限
     */
    public ExpressionVO row;
    /**
     * 列权限
     */
    public List<DataRuleColVO> col;

    public String getSysKey() {
        return sysKey;
    }

    public void setSysKey(String sysKey) {
        this.sysKey = sysKey;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public ExpressionVO getRow() {
        return row;
    }

    public void setRow(ExpressionVO row) {
        this.row = row;
    }

    public List<DataRuleColVO> getCol() {
        return col;
    }

    public void setCol(List<DataRuleColVO> col) {
        this.col = col;
    }
}
