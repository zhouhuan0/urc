package com.yks.urc.lock;

import java.util.concurrent.TimeUnit;

/**
 * 
 * @author wujianghui@youkeshu.com
 * @date 2018年6月12日
 */
public interface DistributedLock
{
	void lock();
	
	void unlock();
	
	boolean tryLock();
	
	boolean tryLock(long time, TimeUnit unit);
}