/*
 * 文件名：UpdateSystemVO.java
 * 版权：Copyright by www.youkeshu.com
 * 描述：
 * 创建人：zhouhuan
 * 创建时间：2020/12/1
 * 修改理由：
 * 修改内容：
 */
package com.yks.urc.vo;

import java.util.List;

/**
 * @author zhouhuan
 * @version 1.0
 * @date 2020/12/1
 * @see UpdateSystemVO
 * @since JDK1.8
 */
public class UpdateSystemVO {
    //数据管理员
    public List<String> dataAdministrators;

    //功能管理员
    public List<String> functionAdministrators;

    //备注
    public String remark;

    public String sysKey;

    public String sysName;

    //系统状态
    public Integer status;

    public Integer isInternalSystem;

    public Integer sysType;

    public String createdTime;
}
