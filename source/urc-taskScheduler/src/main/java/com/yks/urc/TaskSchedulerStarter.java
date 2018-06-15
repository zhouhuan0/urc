package com.yks.urc;

import com.yks.urc.constant.Constant;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//@SpringBootApplication
//@EnableScheduling
public class TaskSchedulerStarter {
	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(TaskSchedulerStarter.class);

	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				new String[]{Constant.TASKSERVICE_STARTER_CONFIG});
		LOGGER.info("Urc Task Scheduler start...");
	}
}
