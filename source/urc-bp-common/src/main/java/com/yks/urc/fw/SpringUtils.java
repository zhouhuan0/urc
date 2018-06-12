package com.yks.urc.fw;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @ClassName: SpringUtils
 * @Description: spring工具类
 * @author LiuYongren
 * @date 2018年1月30日 上午11:22:34
 * @version V1.0
 */
public class SpringUtils
{
	private static final ConcurrentHashMap<String, ApplicationContext> SPRING_CACHE = new ConcurrentHashMap<String, ApplicationContext>();

	private static final Lock LOCK = new ReentrantLock();

	private static final Logger LOGGER = Logger.getLogger(SpringUtils.class);

	/**
	 * 
	 * @Title: getBean 
	 * @Description: 获取bean
	 * @return T bean
	 * @throws 方法异常
	 */
	public static <T> T getBean(String springLocation, String beanName, Class<T> clazz)
	{
		return getSpringContext(springLocation).getBean(beanName, clazz);
	}

	/**
	 * 
	 * @Title: getSpringContext 
	 * @Description: 获取spring上下文信息
	 * @return ApplicationContext spring上下文
	 * @throws 方法异常
	 */
	public static ApplicationContext getSpringContext(String springLocation)
	{
		ApplicationContext spring = SPRING_CACHE.get(springLocation);

		if (null == spring)
		{
			LOCK.lock();

			try
			{
				spring = SPRING_CACHE.get(springLocation);

				if (null == spring)
				{
					spring = new ClassPathXmlApplicationContext(springLocation)
					{
						@Override
						protected DefaultListableBeanFactory createBeanFactory()
						{
							DefaultListableBeanFactory beanFactory = super.createBeanFactory();
							
							return beanFactory;
						}
					};

					SPRING_CACHE.put(springLocation, spring);
				}
			}
			catch (Exception e)
			{
				LOGGER.error(e.toString(), e);
			}
			finally
			{
				LOCK.unlock();
			}
		}
		
		return spring;
	}
}
