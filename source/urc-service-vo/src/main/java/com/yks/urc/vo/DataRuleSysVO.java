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

import com.alibaba.fastjson.JSONObject;

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
    public JSONObject row;
    
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


    public List<DataRuleColVO> getCol() {
        return col;
    }

    public void setCol(List<DataRuleColVO> col) {
        this.col = col;
    }

	public JSONObject getRow() {
		return row;
	}

	public void setRow(JSONObject row) {
		this.row = row;
	}


    
    
}
