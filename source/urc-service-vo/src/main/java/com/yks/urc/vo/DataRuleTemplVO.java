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
     *  用户名
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


}
