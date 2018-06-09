package com.yks.urc.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserInfoTaskTest {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoTaskTest.class);


    @Scheduled(cron = "0/5 * * * * ?")
    public void executeFileDownLoadTask() {

        // 间隔2分钟,执行任务
        Thread current = Thread.currentThread();
        System.out.println("定时任务1:" + current.getId());
        logger.info("ScheduledTest.executeFileDownLoadTask 定时任务1:" + current.getId() + ",name:" + current.getName());
    }
}
