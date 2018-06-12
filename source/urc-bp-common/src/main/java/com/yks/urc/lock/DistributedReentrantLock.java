package com.yks.urc.lock;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.yks.urc.fw.SpringUtils;


/**
 *  分布式可重入锁
 * @author wujianghui@youkeshu.com
 * @date 2018年6月12日
 */
public class DistributedReentrantLock implements DistributedLock
{
	private final InterProcessMutex lock;
	
	public DistributedReentrantLock(String resourceName)
	{
		CuratorFramework client = SpringUtils.getBean("/spring-lock.xml", "curatorFramework",
		        CuratorFramework.class);
		
		lock = new InterProcessMutex(client, "/oms-lock/" + resourceName + "_lock");
	}

	@Override
	public void lock()
	{
		try
		{
			lock.acquire();
		}
		catch (Exception e)
		{
		}
	}

	@Override
	public void unlock()
	{
		try
		{
			lock.release();
		}
		catch (Exception e)
		{
		}
	}

	@Override
	public boolean tryLock()
	{
		return tryLock(0, TimeUnit.MILLISECONDS);
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit)
	{
		try
		{
			return lock.acquire(time, unit);
		}
		catch (Exception e)
		{
		}

		return false;
	}

}
