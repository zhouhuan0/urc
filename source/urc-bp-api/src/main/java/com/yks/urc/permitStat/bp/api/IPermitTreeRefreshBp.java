package com.yks.urc.permitStat.bp.api;

import com.yks.urc.vo.SystemRootVO;

public interface IPermitTreeRefreshBp {
    /**
     * // 更新所有节点的name/sortIdx/url
     * // 因为角色下的json与 urc_permission 表最新json定义可能会不一样
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2020-07-22 11:56
     */
    void refreshNewestFields(SystemRootVO cur);
}
