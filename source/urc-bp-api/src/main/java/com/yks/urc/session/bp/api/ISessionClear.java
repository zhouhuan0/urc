package com.yks.urc.session.bp.api;

public interface ISessionClear {
    /**
     * 移除线程变量,在程序的顶级入口调用
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2019-10-28 10:39
     */
    void removeThreadLocal();
}
