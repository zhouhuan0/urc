package com.yks.urc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskSchedulerApplication {
	private static final Logger logger = LoggerFactory.getLogger(TaskSchedulerApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(TaskSchedulerApplication.class, args);
		logger.info("TaskSchedulerApplication started");
	}
}
