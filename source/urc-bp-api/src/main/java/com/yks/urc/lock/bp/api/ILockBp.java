package com.yks.urc.lock.bp.api;

public interface ILockBp {

    void unlock(String lockName);

    Boolean tryLock(String lockName);
}
