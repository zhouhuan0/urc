package com.yks.urc.lock.bp.api;

public interface ILockBp {
    /**
     * 获取分仓锁,60s过期
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2019/2/26 16:39
     */
    Boolean tryLockSeparateWarehouse(String yksOrderId);

    /**
     * 释放分仓锁
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2019/2/26 16:40
     */
    void unlockSeparateWarehouse(String yksOrderId);

    void unlock(String lockName);

    Boolean tryLock(String lockName);
}
