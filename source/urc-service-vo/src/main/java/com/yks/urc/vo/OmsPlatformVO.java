/**
 * 〈一句话功能简述〉<br>
 * 〈平台基础信息〉
 *
 * @author 31076
 * @create 2018/6/5
 * @since 1.0.0
 */
package com.yks.urc.vo;

import java.io.Serializable;
import java.util.List;

public class OmsPlatformVO implements Serializable {
    private static final long serialVersionUID = 7915177115475857675L;

    /**
     * 平台名称
     */
    public String platformName;
    /**
     * 平台ID:oms传的和platCode一样，其它系统用的是老账号管理系统的平台编码
     */
    public String platformId;

    /**
     * 2位平台编码
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2020-07-29 10:11
     */
    public String platCode;

    /**
     * 账号
     */
    public List<OmsShopVO> lstShop;

    public Boolean isAll;
}
