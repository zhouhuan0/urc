package com.yks.pls.task.quatz;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.List;

//@Component
public class QuatzBootstrap implements ApplicationContextAware {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private Integer threadCount=15;

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    private String threadNamePrefix="pls";

    @PostConstruct
    public void init() {
        try {
            DaemonJob.addDaemonJob(threadNamePrefix,threadCount);
        } catch (Exception ex) {
            logger.error("init", ex);
        }
    }

    static ApplicationContext ctx = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

    public static List<PlsEbayTaskDO> getTaskDO() {
        return ctx.getBean(ITaskProvider.class).getTaskDO();
    }

    public static void setLastExecuteTime(PlsEbayTaskDO taskDO){
        ctx.getBean(ITaskProvider.class).setLastExecuteTime(taskDO);
    }

    /**
     * 找不到bean返回null
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2019/4/24 9:23
     */
    public static <T> T getBeanNoException(Class<T> clazz) {
        try {
            return ctx.getBean(clazz);
        } catch (Exception ex) {
            return null;
        }
    }

    public static <T> T getBean(Class<T> var1) throws BeansException{
        return ctx.getBean(var1);
    }
}
